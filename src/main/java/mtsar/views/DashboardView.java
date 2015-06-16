package mtsar.views;

import io.dropwizard.views.View;
import mtsar.MechanicalTsarVersion;
import mtsar.api.Process;
import mtsar.api.jdbi.AnswerDAO;
import mtsar.api.jdbi.TaskDAO;
import mtsar.api.jdbi.WorkerDAO;

import javax.inject.Inject;
import java.util.Map;

public class DashboardView extends View {
    private final MechanicalTsarVersion version;
    private final Map<String, Process> processes;
    private final TaskDAO taskDAO;
    private final WorkerDAO workerDAO;
    private final AnswerDAO answerDAO;

    @Inject
    public DashboardView(MechanicalTsarVersion version, Map<String, Process> processes, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        super("dashboard.mustache");
        this.version = version;
        this.processes = processes;
        this.taskDAO = taskDAO;
        this.workerDAO = workerDAO;
        this.answerDAO = answerDAO;
    }

    public String getTitle() {
        return "Dashboard";
    }

    public String getVersion() {
        return version.getVersion();
    }

    public String getJvm() {
        return System.getProperty("java.runtime.version");
    }

    public int getProcessCount() {
        return processes.size();
    }

    public int getWorkerCount() {
        return processes.values().stream().
                map(process -> workerDAO.count(process.getId())).
                reduce(0, (r, e) -> r + e);
    }

    public int getTaskCount() {
        return processes.values().stream().
                map(process -> taskDAO.count(process.getId())).
                reduce(0, (r, e) -> r + e);
    }

    public int getAnswerCount() {
        return processes.values().stream().
                map(process -> answerDAO.count(process.getId())).
                reduce(0, (r, e) -> r + e);
    }
}
