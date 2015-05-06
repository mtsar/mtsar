package mtsar.resources;

import io.dropwizard.jersey.PATCH;
import mtsar.api.Answer;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.Worker;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {
    final protected mtsar.api.Process process;

    public TaskResource(Process process) {
        this.process = process;
    }

    @GET
    public List<Task> getTasks(@QueryParam("page") @DefaultValue("0") int page) {
        return process.getTaskDAO().listForProcess(process.getId());
    }

    @POST
    public Task postTask() {
        Task t = Task.builder().
                setProcess(process.getId()).
                setDateTime(Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())).
                build();
        int taskId = process.getTaskDAO().insert(t);
        return process.getTaskDAO().find(taskId, process.getId());
    }

    @GET
    @Path("{task}")
    public Task getTask(@PathParam("task") Integer id) {
        return process.getTaskDAO().find(id, process.getId());
    }

    @GET
    @Path("{task}/answers")
    public List<Answer> getTaskAnswers(@PathParam("task") Integer id) {
        throw new WebApplicationException(Response.Status.NOT_IMPLEMENTED);
    }

    @POST
    @Path("{task}/answer")
    public Answer postTaskAnswer(@PathParam("task") Integer id, @FormParam("worker_id") Integer workerId, @FormParam("answer") String answer) {
        final Worker w = fetchWorker(workerId);
        final Task t = fetchTask(id);
        final Answer a = Answer.builder().
                setProcess(process.getId()).
                setTaskId(t.getId()).
                setWorkerId(w.getId()).
                setAnswer(answer).
                setDateTime(Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())).
                build();
        int answerId = process.getAnswerDAO().insert(a);
        return process.getAnswerDAO().find(answerId, process.getId());
    }

    @PATCH
    @Path("{task}")
    public Task patchTask(@PathParam("task") String task) {
        throw new WebApplicationException(Response.Status.NOT_IMPLEMENTED);
    }

    @DELETE
    @Path("{task}")
    public Task deleteTask(@PathParam("task") String task) {
        throw new WebApplicationException(Response.Status.NOT_IMPLEMENTED);
    }

    private Worker fetchWorker(Integer id) {
        Worker w = process.getWorkerDAO().find(id, process.getId());
        if (w == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return w;
    }

    private Task fetchTask(Integer id) {
        Task t = process.getTaskDAO().find(id, process.getId());
        if (t == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return t;
    }
}
