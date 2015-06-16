package mtsar.resources;

import mtsar.api.Answer;
import mtsar.api.Process;
import mtsar.api.jdbi.AnswerDAO;
import mtsar.api.jdbi.TaskDAO;
import mtsar.api.jdbi.WorkerDAO;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @POST
    public Answer postAnswer(@FormParam("worker") String worker, @FormParam("task") String task, @FormParam("processors") String answerParam, @FormParam("timestamp") String timestampParam) {
        final Timestamp timestamp = (timestampParam == null) ?
                Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) :
                Timestamp.valueOf(timestampParam);
        final Answer answer = Answer.builder().
                setProcess(process.getId()).
                setAnswer(answerParam).
                setDateTime(timestamp).
                build();
        int answerId = answerDAO.insert(answer);
        return answerDAO.find(answerId, process.getId());
    }
}
