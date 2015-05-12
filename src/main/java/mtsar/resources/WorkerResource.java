package mtsar.resources;

import mtsar.api.Answer;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.Worker;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
public class WorkerResource {
    final protected Process process;

    public WorkerResource(Process process) {
        this.process = process;
    }

    @GET
    public List<Worker> getWorkers() {
        return process.getWorkerDAO().listForProcess(process.getId());
    }

    @POST
    public Worker postWorker() {
        Worker w = Worker.builder().
                setProcess(process.getId()).
                setDateTime(Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())).
                build();
        int workerId = process.getWorkerDAO().insert(w);
        return process.getWorkerDAO().find(workerId, process.getId());
    }

    @GET
    @Path("{worker}")
    public Worker getWorker(@PathParam("worker") Integer id) {
        return fetchWorker(id);
    }

    @GET
    @Path("{worker}/task")
    public Task getWorkerTask(@PathParam("worker") Integer id) {
        final Worker w = fetchWorker(id);
        return process.getTaskAllocator().allocate(w).get();
    }

    @GET
    @Path("{worker}/answers")
    public List<Answer> getWorkerAnswers(@PathParam("worker") Integer id) {
        final Worker w = fetchWorker(id);
        return process.getAnswerDAO().listForWorker(w.getId(), process.getId());
    }

    @DELETE
    @Path("{worker}")
    public Worker deleteWorker(@PathParam("worker") Integer id) {
        final Worker w = fetchWorker(id);
        throw new WebApplicationException(Response.Status.NOT_IMPLEMENTED);
    }

    private Worker fetchWorker(Integer id) {
        Worker w = process.getWorkerDAO().find(id, process.getId());
        if (w == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return w;
    }
}
