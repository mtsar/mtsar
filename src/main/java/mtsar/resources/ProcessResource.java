package mtsar.resources;

import mtsar.api.Process;
import mtsar.views.ProcessView;
import mtsar.views.ProcessesView;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Singleton
@Path("/processes")
@Produces(MediaType.APPLICATION_JSON)
public class ProcessResource {
    protected final Map<String, Process> processes;

    @Inject
    public ProcessResource(Map<String, Process> processes) {
        this.processes = processes;
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
        return new ProcessView(fetchProcess(id));
    }

    @Path("{process}/workers")
    public WorkerResource getWorkers(@PathParam("process") String id) {
        return new WorkerResource(fetchProcess(id));
    }

    @Path("{process}/tasks")
    public TaskResource getTasks(@PathParam("process") String id) {
        return new TaskResource(fetchProcess(id));
    }

    @Path("{process}/answers")
    public AnswerResource getAnswers(@PathParam("process") String id) {
        return new AnswerResource(fetchProcess(id));
    }

    protected Process fetchProcess(String id) {
        if (!processes.containsKey(id)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return processes.get(id);
    }
}
