package mtsar.views;

import io.dropwizard.views.View;
import mtsar.MechanicalTsarVersion;
import mtsar.api.*;
import mtsar.api.Process;

import javax.inject.Inject;
import java.util.Map;

public class DashboardView extends View {
    private final MechanicalTsarVersion version;
    private final Map<String, Process> processes;

    @Inject
    public DashboardView(MechanicalTsarVersion version, Map<String, Process> processes) {
        super("dashboard.mustache");
        this.version = version;
        this.processes = processes;
    }

    public String getTitle() { return "Dashboard"; }

    public String getVersion() { return version.getVersion(); }

    public String getJvm() {
        return System.getProperty("java.runtime.version");
    }

    public int getProcessCount() {
        return processes.size();
    }

    public int getWorkerCount() {
        return processes.values().stream().
                map(process -> process.getWorkerDAO().count(process.getId())).
                reduce(0, (r, e) -> r + e);
    }

    public int getTaskCount() {
        return processes.values().stream().
                map(process -> process.getTaskDAO().count(process.getId())).
                reduce(0, (r, e) -> r + e);
    }

    public int getAnswerCount() {
        return processes.values().stream().
                map(process -> process.getAnswerDAO().count(process.getId())).
                reduce(0, (r, e) -> r + e);
    }
}
