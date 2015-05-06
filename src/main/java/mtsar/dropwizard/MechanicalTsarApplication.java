package mtsar.dropwizard;

import com.google.inject.Injector;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import mtsar.api.*;
import mtsar.api.Process;
import mtsar.cli.EvaluateCommand;
import mtsar.cli.SimulateCommand;
import mtsar.resources.*;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.ServerProperties;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import java.util.Map;

public class MechanicalTsarApplication extends Application<MechanicalTsarConfiguration> {
    private GuiceBundle<MechanicalTsarConfiguration> guiceBundle;

    public final Injector getInjector() {
        return guiceBundle.getInjector();
    }

    public static void main(String[] args) throws Exception {
        new MechanicalTsarApplication().run(args);
    }

    @Override
    public String getName() {
        return "Mechanical Tsar";
    }

    @Override
    public void initialize(Bootstrap<MechanicalTsarConfiguration> bootstrap) {
        guiceBundle = GuiceBundle.<MechanicalTsarConfiguration>newBuilder()
                .addModule(new MechanicalTsarModule())
                .setConfigClass(MechanicalTsarConfiguration.class)
                .build();

        bootstrap.addBundle(new MigrationsBundle<MechanicalTsarConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(MechanicalTsarConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(guiceBundle);
        bootstrap.addBundle(new AssetsBundle("/mtsar/stylesheets", "/stylesheets", null, "stylesheets"));
        bootstrap.addBundle(new AssetsBundle("/mtsar/javascripts", "/javascripts", null, "javascripts"));
        bootstrap.addBundle(new AssetsBundle("/mtsar/images", "/images", null, "images"));
        bootstrap.addBundle(new AssetsBundle("/mtsar/favicon.ico", "/favicon.ico", null, "favicon"));
        bootstrap.addBundle(new ViewBundle<>());

        bootstrap.addCommand(new EvaluateCommand(this));
        bootstrap.addCommand(new SimulateCommand(this));
    }

    @Override
    public void run(MechanicalTsarConfiguration configuration, Environment environment) throws ClassNotFoundException {
        FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,PATCH,DELETE,OPTIONS");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter(CrossOriginFilter.EXPOSED_HEADERS_PARAM, "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,Location");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,Location");
        filter.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

        environment.jersey().disable(ServerProperties.WADL_FEATURE_DISABLE);
        environment.jersey().register(getInjector().getInstance(MetaResource.class));
        environment.jersey().register(getInjector().getInstance(ProcessResource.class));

        environment.healthChecks().register("version", getInjector().getInstance(MechanicalTsarVersionHealthCheck.class));

        for (Map.Entry<String, Process> entry: configuration.getProcesses().entrySet()) {
            final String id = entry.getKey();
            final Process process = entry.getValue();
            process.setId(id);
            process.bootstrap(getInjector());
        }
    }
}
