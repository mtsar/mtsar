package mtsar.resources;

import mtsar.api.*;
import mtsar.api.Process;

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
    public Answer postAnswer(@FormParam("worker") String worker, @FormParam("task") String task, @FormParam("processors") String answer) {
        Answer a = Answer.builder().
                setProcess(process.getId()).
                setDateTime(Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())).
                build();
        int answerId = process.getAnswerDAO().insert(a);
        return process.getAnswerDAO().find(answerId, process.getId());
    }
}
