package mtsar.views;

import io.dropwizard.views.View;
import mtsar.api.Process;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

public class ProcessesView extends View {
    private final Map<String, Process> processes;

    @Inject
    public ProcessesView(Map<String, Process> processes) {
        super("processes.mustache");
        this.processes = processes;
    }

    public String getTitle() {
        return "Processes";
    }

    public Collection<Process> getProcesses() {
        return processes.values();
    }
}
