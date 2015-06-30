package mtsar.views;

import com.google.common.base.Function;
import io.dropwizard.views.View;
import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

public class ProcessesView extends View {
    private final Map<String, Process> processes;
    private final TaskDAO taskDAO;
    private final WorkerDAO workerDAO;
    private final AnswerDAO answerDAO;

    @Inject
    public ProcessesView(Map<String, Process> processes, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        super("processes.mustache");
        this.processes = processes;
        this.taskDAO = taskDAO;
        this.workerDAO = workerDAO;
        this.answerDAO = answerDAO;
    }

    public String getTitle() {
        return "Processes";
    }

    public Collection<Process> getProcesses() {
        return processes.values();
    }

    public Function<String, Integer> getWorkerCount() {
        return id -> workerDAO.count(id);
    }

    public Function<String, Integer> getTaskCount() {
        return id -> taskDAO.count(id);
    }

    public Function<String, Integer> getAnswerCount() {
        return id -> answerDAO.count(id);
    }
}
