package mtsar.cli;

import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import mtsar.api.*;
import mtsar.api.Process;
import mtsar.dropwizard.MechanicalTsarApplication;
import mtsar.dropwizard.MechanicalTsarConfiguration;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.Map;

public class SimulateCommand extends EnvironmentCommand<MechanicalTsarConfiguration> {
    private final MechanicalTsarApplication application;

    public SimulateCommand(Application<MechanicalTsarConfiguration> application) {
        super(application, "simulate", "Runs the simulation");
        this.application = (MechanicalTsarApplication) application;
    }

    protected void run(Environment environment, Namespace namespace, MechanicalTsarConfiguration configuration) {
        configuration.getProcesses().forEach((id, process) -> {
            process.setId(id);
            try {
                process.bootstrap(application.getInjector());
            }
            catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        });
    }
}