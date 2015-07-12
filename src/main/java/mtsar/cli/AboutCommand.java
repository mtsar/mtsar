package mtsar.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import mtsar.MechanicalTsarVersion;
import mtsar.dropwizard.MechanicalTsarApplication;
import mtsar.dropwizard.MechanicalTsarConfiguration;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;

public class AboutCommand extends EnvironmentCommand<MechanicalTsarConfiguration> {
    private final MechanicalTsarApplication application;

    public AboutCommand(Application<MechanicalTsarConfiguration> application) {
        super(application, "about", "Print the system environment");
        this.application = (MechanicalTsarApplication) application;
    }

    protected void run(Environment environment, Namespace namespace, MechanicalTsarConfiguration configuration) throws IOException {
        final MechanicalTsarVersion version = application.getInjector().getInstance(MechanicalTsarVersion.class);
        System.out.format("Mechanical Tsar version %s%n", version.toString());
        System.out.format("Java version %s%n", System.getProperty("java.runtime.version"));
        System.out.println();
        System.out.format("Configuration: %s%n", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(configuration));
        System.out.println();
        System.out.format("Processes: %s%n", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(application.getProcesses()));
        System.out.flush();
    }
}
