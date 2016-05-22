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
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import mtsar.api.Stage;
import mtsar.cli.AboutCommand;
import mtsar.cli.ConsoleCommand;
import mtsar.cli.EvaluateCommand;
import mtsar.cli.SimulateCommand;
import mtsar.dropwizard.hk2.ApplicationBinder;
import mtsar.resources.MetaResource;
import mtsar.resources.StageResource;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ServerProperties;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.validation.Validator;
import java.util.EnumSet;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Mechanical Tsar is an engine for mechanized labor workflows.
 */
public class MechanicalTsarApplication extends Application<MechanicalTsarConfiguration> {
    private ApplicationBinder binder;

    public static void main(String[] args) throws Exception {
        new MechanicalTsarApplication().run(args);
    }

    @Override
    public String getName() {
        return "Mechanical Tsar";
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

    @Override
    public void run(MechanicalTsarConfiguration configuration, Environment environment) throws ClassNotFoundException {
        binder = new ApplicationBinder(configuration, environment);

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
        environment.jersey().register(requireNonNull(binder.getLocator().getService(MetaResource.class)));
        environment.jersey().register(requireNonNull(binder.getLocator().getService(StageResource.class)));

        environment.healthChecks().register("version", requireNonNull(binder.getLocator().getService(MechanicalTsarVersionHealthCheck.class)));
    }

    public Map<String, Stage> getStages() {
        return binder.getStages();
    }

    private static class MechanicalTsarMigrationsBundle extends MigrationsBundle<MechanicalTsarConfiguration> {
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
}
