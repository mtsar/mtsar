package mtsar.dropwizard.guice;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import mtsar.api.Process;

import java.util.Map;

public class BundleModule extends AbstractModule {
    public final static TypeLiteral<Map<String, mtsar.api.Process>> PROCESSES_TYPE_LITERAL = new TypeLiteral<Map<String, Process>>() {
    };

    private Map<String, Process> processes;

    public BundleModule(Map<String, Process> processes) {
        this.processes = processes;
    }

    @Override
    protected void configure() {
        bind(PROCESSES_TYPE_LITERAL).toInstance(processes);
    }
}
