package mtsar.dropwizard.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import mtsar.api.Process;
import mtsar.dropwizard.MechanicalTsarConfiguration;

import java.util.Map;

public class Module extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    public Map<String, Process> getProcesses(MechanicalTsarConfiguration configuration) {
        return configuration.getProcesses();
    }
}
