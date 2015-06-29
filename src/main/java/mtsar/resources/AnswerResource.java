package mtsar.resources;

import io.dropwizard.jersey.PATCH;
import mtsar.api.Answer;
import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
public class AnswerResource {
    protected final Process process;
    protected final TaskDAO taskDAO;
    protected final WorkerDAO workerDAO;
    protected final AnswerDAO answerDAO;

    public AnswerResource(Process process, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        this.process = process;
        this.taskDAO = taskDAO;
        this.workerDAO = workerDAO;
        this.answerDAO = answerDAO;
    }

    @GET
    public List<Answer> getAnswers() {
        return answerDAO.listForProcess(process.getId());
    }

    @GET
    @Path("{answer}")
    public Answer getAnswer(@PathParam("answer") Integer id) {
        return fetchAnswer(id);
    }

    @PATCH
    @Path("{answer}")
    public Answer patchAnswer(@PathParam("answer") Integer id) {
        final Answer answer = fetchAnswer(id);
        throw new WebApplicationException(Response.Status.NOT_IMPLEMENTED);
    }

    @DELETE
    @Path("{answer}")
    public Answer deleteAnswer(@PathParam("answer") Integer id) {
        final Answer answer = fetchAnswer(id);
        answerDAO.delete(id, process.getId());
        return answer;
    }

    @DELETE
    public void deleteAnswers() {
        answerDAO.deleteAll(process.getId());
    }

    private Answer fetchAnswer(Integer id) {
        final Answer answer = answerDAO.find(id, process.getId());
        if (answer == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
        return answer;
    }
}
