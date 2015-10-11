/*
 * Copyright 2015 Dmitry Ustalov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mtsar.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import mtsar.api.Stage;
import mtsar.api.sql.StageDAO;
import mtsar.cli.AboutCommand;
import mtsar.cli.ConsoleCommand;
import mtsar.cli.EvaluateCommand;
import mtsar.cli.SimulateCommand;
import mtsar.dropwizard.hk2.ApplicationBinder;
import mtsar.dropwizard.hk2.StageBinder;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.TaskAllocator;
import mtsar.processors.WorkerRanker;
import mtsar.resources.MetaResource;
import mtsar.resources.StageResource;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.server.ServerProperties;
import org.skife.jdbi.v2.DBI;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.validation.Validator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Mechanical Tsar is an engine for mechanized labor workflows.
 */
public class MechanicalTsarApplication extends Application<MechanicalTsarConfiguration> {
    static class MechanicalTsarMigrationsBundle extends MigrationsBundle<MechanicalTsarConfiguration> {
        @Override
        public DataSourceFactory getDataSourceFactory(MechanicalTsarConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    }

    static class ValidatorBinder extends AbstractBinder {
        private final Environment environment;

        public ValidatorBinder(Environment environment) {
            this.environment = environment;
        }

        @Override
        protected void configure() {
            bind(environment.getValidator()).to(Validator.class);
        }
    }

    private final Map<String, Stage> processes = new HashMap<>();

    private DBI jdbi;
    private ServiceLocator locator;

    public static void main(String[] args) throws Exception {
        new MechanicalTsarApplication().run(args);
    }

    @Override
    public String getName() {
        return "Mechanical Tsar";
    }

    public ServiceLocator getLocator() {
        return locator;
    }

    public Map<String, Stage> getStages() {
        return processes;
    }

    @Override
    public void initialize(Bootstrap<MechanicalTsarConfiguration> bootstrap) {
        bootstrap.addBundle(new MechanicalTsarMigrationsBundle());

        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new AssetsBundle("/mtsar/stylesheets", "/stylesheets", null, "stylesheets"));
        bootstrap.addBundle(new AssetsBundle("/mtsar/javascripts", "/javascripts", null, "javascripts"));
        bootstrap.addBundle(new AssetsBundle("/mtsar/images", "/images", null, "images"));
        bootstrap.addBundle(new AssetsBundle("/mtsar/favicon.ico", "/favicon.ico", null, "favicon"));
        bootstrap.addBundle(new AssetsBundle("/mtsar/robots.txt", "/robots.txt", null, "robots"));
        bootstrap.addBundle(new AssetsBundle("/META-INF/resources/webjars", "/assets", null, "assets"));
        bootstrap.addBundle(new ViewBundle<>());

        bootstrap.addCommand(new EvaluateCommand(this));
        bootstrap.addCommand(new SimulateCommand(this));
        bootstrap.addCommand(new ConsoleCommand(this));
        bootstrap.addCommand(new AboutCommand(this));
    }

    public void bootstrap(MechanicalTsarConfiguration configuration, Environment environment) throws ClassNotFoundException {
        synchronized (processes) {
            if (jdbi == null)
                jdbi = new DBIFactory().build(environment, configuration.getDataSourceFactory(), "postgresql");

            if (locator == null)
                locator = Injections.createLocator(new ApplicationBinder(jdbi, processes));

            final StageDAO stageDAO = requireNonNull(locator.getService(StageDAO.class));
            final List<Stage.Definition> definitions = stageDAO.select();
            processes.clear();

            for (final Stage.Definition definition : definitions) {
                final Class<? extends WorkerRanker> workerRankerClass = Class.forName(definition.getWorkerRanker()).asSubclass(WorkerRanker.class);
                final Class<? extends TaskAllocator> taskAllocatorClass = Class.forName(definition.getTaskAllocator()).asSubclass(TaskAllocator.class);
                final Class<? extends AnswerAggregator> answerAggregatorClass = Class.forName(definition.getAnswerAggregator()).asSubclass(AnswerAggregator.class);
                final ServiceLocator processLocator = Injections.createLocator(locator, new StageBinder(definition, workerRankerClass, taskAllocatorClass, answerAggregatorClass));
                final Stage stage = requireNonNull(processLocator.getService(Stage.class));
                processes.put(definition.getId(), stage);
            }
        }
    }

    @Override
    public void run(MechanicalTsarConfiguration configuration, Environment environment) throws ClassNotFoundException {
        bootstrap(configuration, environment);

        final FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,PATCH,DELETE,OPTIONS");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter(CrossOriginFilter.EXPOSED_HEADERS_PARAM, "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,Location");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,Location");
        filter.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

        environment.jersey().disable(ServerProperties.WADL_FEATURE_DISABLE);
        environment.jersey().register(new ValidatorBinder(environment));
        environment.jersey().register(requireNonNull(locator.getService(MetaResource.class)));
        environment.jersey().register(requireNonNull(locator.getService(StageResource.class)));

        environment.healthChecks().register("version", requireNonNull(locator.getService(MechanicalTsarVersionHealthCheck.class)));
    }
}
