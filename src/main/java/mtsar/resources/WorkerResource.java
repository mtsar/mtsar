package mtsar.resources;

import mtsar.api.*;
import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Produces(MediaType.APPLICATION_JSON)
public class WorkerResource {
    protected final mtsar.api.Process process;
    protected final TaskDAO taskDAO;
    protected final WorkerDAO workerDAO;
    protected final AnswerDAO answerDAO;

    public WorkerResource(Process process, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        this.process = process;
        this.taskDAO = taskDAO;
        this.workerDAO = workerDAO;
        this.answerDAO = answerDAO;
    }

    @GET
    public List<Worker> getWorkers() {
        return workerDAO.listForProcess(process.getId());
    }

    @POST
    public Worker postWorker(@FormParam("external_id") String externalId) {
        Worker w = Worker.builder().
                setExternalId(externalId).
                setProcess(process.getId()).
                setDateTime(Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())).
                build();
        int workerId = workerDAO.insert(w);
        return workerDAO.find(workerId, process.getId());
    }

    @GET
    @Path("{worker}")
    public Worker getWorker(@PathParam("worker") Integer id) {
        return fetchWorker(id);
    }

    @GET
    @Path("external")
    public Worker getWorkerByExternalId(@QueryParam("externalId") String externalId) {
        final Worker worker = workerDAO.findByExternalId(externalId, process.getId());
        if (worker == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
        return worker;
    }

    @GET
    @Path("{worker}/task")
    public TaskAllocation getWorkerTask(@PathParam("worker") Integer id) {
        final Worker w = fetchWorker(id);
        final Optional<TaskAllocation> allocation = process.getTaskAllocator().allocate(w);
        return allocation.isPresent() ? allocation.get() : null;
    }

    @GET
    @Path("{worker}/answers")
    public List<Answer> getWorkerAnswers(@PathParam("worker") Integer id) {
        final Worker w = fetchWorker(id);
        return answerDAO.listForWorker(w.getId(), process.getId());
    }

    @DELETE
    @Path("{worker}")
    public Worker deleteWorker(@PathParam("worker") Integer id) {
        final Worker w = fetchWorker(id);
        throw new WebApplicationException(Response.Status.NOT_IMPLEMENTED);
    }

    private Worker fetchWorker(Integer id) {
        final Worker w = workerDAO.find(id, process.getId());
        if (w == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return w;
    }
}
