package mtsar.resources;

import io.dropwizard.jersey.PATCH;
import mtsar.api.Answer;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.Worker;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
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
    public Task postTask(@FormParam("external_id") String externalId, @FormParam("description") String description, @FormParam("answers") List<String> answers) {
        Task t = Task.builder().
                setExternalId(externalId).
                setType("single").
                setDescription(description).
                setAnswers(answers.toArray(new String[answers.size()])).
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
        return process.getAnswerDAO().listForTask(id, process.getId());
    }

    @GET
    @Path("{task}/answer")
    public Answer getTaskAnswer(@PathParam("task") Integer id) {
        final Task task = fetchTask(id);
        final Optional<Answer> answer = process.getAnswerAggregator().aggregate(task);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Path("{task}/answer")
    public Answer postTaskAnswer(@PathParam("task") Integer id, @FormParam("external_id") String externalId, @FormParam("worker_id") Integer workerId, @FormParam("answer") String answerParam, @FormParam("timestamp") String timestampParam) {
        final Timestamp timestamp = (timestampParam == null) ?
                Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) :
                Timestamp.valueOf(timestampParam);
        final Worker worker = fetchWorker(workerId);
        final Task task = fetchTask(id);
        final Answer answer = Answer.builder().
                setProcess(process.getId()).
                setExternalId(externalId).
                setTaskId(task.getId()).
                setWorkerId(worker.getId()).
                setAnswer(answerParam).
                setDateTime(timestamp).
                build();
        int answerId = process.getAnswerDAO().insert(answer);
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
