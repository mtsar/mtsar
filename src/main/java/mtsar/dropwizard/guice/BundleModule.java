package mtsar.dropwizard.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import io.dropwizard.setup.Environment;
import mtsar.api.Process;

import javax.validation.Validator;
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
        bind(PROCESSES_TYPE_LITERAL).annotatedWith(Names.named("processes")).toInstance(processes);
    }

    @Provides
    public Validator getValidator(Environment environment) {
        return environment.getValidator();
    }
}
