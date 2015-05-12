package mtsar.resources;

import mtsar.api.*;
import mtsar.api.Process;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
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
public class AnswerResource {
    final protected Process process;

    public AnswerResource(Process process) {
        this.process = process;
    }

    @GET
    public List<Answer> getAnswers() {
        return process.getAnswerDAO().listForProcess(process.getId());
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
        int answerId = process.getAnswerDAO().insert(answer);
        return process.getAnswerDAO().find(answerId, process.getId());
    }
}
