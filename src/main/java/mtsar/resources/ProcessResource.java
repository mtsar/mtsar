package mtsar.resources;

import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.views.ProcessView;
import mtsar.views.ProcessesView;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Singleton
@Path("/processes")
@Produces(MediaType.APPLICATION_JSON)
public class ProcessResource {
    protected final Map<String, mtsar.api.Process> processes;
    protected final TaskDAO taskDAO;
    protected final WorkerDAO workerDAO;
    protected final AnswerDAO answerDAO;

    @Inject
    public ProcessResource(@Named("processes") Map<String, Process> processes, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        this.processes = processes;
        this.taskDAO = taskDAO;
        this.workerDAO = workerDAO;
        this.answerDAO = answerDAO;
    }

    @GET
    public Map<String, Process> getProcesses() {
        return processes;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public ProcessesView getProcessView() {
        return new ProcessesView(processes);
    }

    @GET
    @Path("{process}")
    public Process getProcess(@PathParam("process") String id) {
        return fetchProcess(id);
    }

    @GET
    @Path("{process}")
    @Produces(MediaType.TEXT_HTML)
    public ProcessView getProcessView(@PathParam("process") String id) {
        return new ProcessView(fetchProcess(id), taskDAO, workerDAO, answerDAO);
    }

    @Path("{process}/workers")
    public WorkerResource getWorkers(@PathParam("process") String id) {
        return new WorkerResource(fetchProcess(id), taskDAO, workerDAO, answerDAO);
    }

    @Path("{process}/tasks")
    public TaskResource getTasks(@PathParam("process") String id) {
        return new TaskResource(fetchProcess(id), taskDAO, workerDAO, answerDAO);
    }

    @Path("{process}/answers")
    public AnswerResource getAnswers(@PathParam("process") String id) {
        return new AnswerResource(fetchProcess(id), taskDAO, workerDAO, answerDAO);
    }

    protected Process fetchProcess(String id) {
        if (!processes.containsKey(id)) throw new WebApplicationException(Response.Status.NOT_FOUND);
        return processes.get(id);
    }
}
