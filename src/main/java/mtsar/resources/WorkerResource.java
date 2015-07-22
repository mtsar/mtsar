package mtsar.resources;

import io.dropwizard.jersey.PATCH;
import mtsar.api.Answer;
import mtsar.api.Process;
import mtsar.api.TaskAllocation;
import mtsar.api.Worker;
import mtsar.api.csv.WorkerCSVWriter;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Path("/workers")
@Produces(mtsar.MediaType.APPLICATION_JSON)
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

    @GET
    @Produces(mtsar.MediaType.TEXT_CSV)
    public StreamingOutput getWorkersCSV() {
        final List<Worker> workers = workerDAO.listForProcess(process.getId());
        return output -> WorkerCSVWriter.write(workers, output);
    }

    @POST
    public Response postWorker(@Context UriInfo uriInfo, @FormParam("external_id") String externalId) {
        int workerId = workerDAO.insert(Worker.builder().
                setExternalId(externalId).
                setProcess(process.getId()).
                setDateTime(Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())).
                build());
        final Worker worker = workerDAO.find(workerId, process.getId());
        return Response.created(getWorkerURI(uriInfo, worker)).entity(worker).build();
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
        final Worker worker = fetchWorker(id);
        final Optional<TaskAllocation> allocation = process.getTaskAllocator().allocate(worker);
        return allocation.isPresent() ? allocation.get() : null;
    }

    @GET
    @Path("{worker}/answers")
    public List<Answer> getWorkerAnswers(@PathParam("worker") Integer id) {
        final Worker worker = fetchWorker(id);
        return answerDAO.listForWorker(worker.getId(), process.getId());
    }

    @PATCH
    @Path("{worker}")
    public Worker patchWorker(@PathParam("worker") Integer id) {
        final Worker worker = fetchWorker(id);
        throw new WebApplicationException(Response.Status.NOT_IMPLEMENTED);
    }

    @DELETE
    @Path("{worker}")
    public Worker deleteWorker(@PathParam("worker") Integer id) {
        final Worker worker = fetchWorker(id);
        workerDAO.delete(id, process.getId());
        return worker;
    }

    @DELETE
    public void deleteTasks() {
        workerDAO.deleteAll(process.getId());
    }

    private Worker fetchWorker(Integer id) {
        final Worker worker = workerDAO.find(id, process.getId());
        if (worker == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
        return worker;
    }

    private URI getWorkerURI(UriInfo uriInfo, Worker worker) {
        return uriInfo.getBaseUriBuilder().
                path("processes").path(process.getId()).
                path("workers").path(worker.getId().toString()).
                build();
    }
}
