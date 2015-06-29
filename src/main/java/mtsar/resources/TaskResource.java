package mtsar.resources;

import io.dropwizard.jersey.PATCH;
import mtsar.TaskCSVParser;
import mtsar.api.*;
import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {
    protected final Process process;
    protected final TaskDAO taskDAO;
    protected final WorkerDAO workerDAO;
    protected final AnswerDAO answerDAO;

    public TaskResource(Process process, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        this.process = process;
        this.taskDAO = taskDAO;
        this.workerDAO = workerDAO;
        this.answerDAO = answerDAO;
    }

    @GET
    public List<Task> getTasks(@QueryParam("page") @DefaultValue("0") int page) {
        return taskDAO.listForProcess(process.getId());
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
        int taskId = taskDAO.insert(t);
        return taskDAO.find(taskId, process.getId());
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void postTasks(@FormDataParam("file") File file) throws IOException {
        TaskCSVParser.insert(file, process, taskDAO);
    }

    @GET
    @Path("{task}")
    public Task getTask(@PathParam("task") Integer id) {
        return taskDAO.find(id, process.getId());
    }

    @GET
    @Path("{task}/answers")
    public List<Answer> getTaskAnswers(@PathParam("task") Integer id) {
        return answerDAO.listForTask(id, process.getId());
    }

    @GET
    @Path("{task}/answer")
    public Answer getTaskAnswer(@PathParam("task") Integer id) {
        final Task task = fetchTask(id);
        final Optional<AnswerAggregation> aggregation = process.getAnswerAggregator().aggregate(task);
        if (aggregation.isPresent()) {
            return aggregation.get().getAnswer();
        } else {
            throw new WebApplicationException(Response.Status.NO_CONTENT);
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
        int answerId = answerDAO.insert(answer);
        return answerDAO.find(answerId, process.getId());
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

    @DELETE
    public void deleteTasks() {
        taskDAO.deleteAll(process.getId());
    }

    private Worker fetchWorker(Integer id) {
        final Worker w = workerDAO.find(id, process.getId());
        if (w == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return w;
    }

    private Task fetchTask(Integer id) {
        final Task t = taskDAO.find(id, process.getId());
        if (t == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return t;
    }
}
