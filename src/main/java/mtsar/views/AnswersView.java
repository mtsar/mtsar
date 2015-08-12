package mtsar.views;

import io.dropwizard.views.View;
import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

public class AnswersView extends View {
    private final UriInfo uriInfo;
    private final Process process;
    private final AnswerDAO answerDAO;

    @Inject
    public AnswersView(UriInfo uriInfo, Process process, AnswerDAO answerDAO) {
        super("answers.mustache");
        this.uriInfo = uriInfo;
        this.process = process;
        this.answerDAO = answerDAO;
    }

    public String getTitle() {
        return String.format("Answers of \"%s\"", process.getId());
    }

    public Process getProcess() {
        return process;
    }

    public int getAnswerCount() {
        return answerDAO.count(process.getId());
    }

    public String getProcessPath() {
        return uriInfo.getBaseUriBuilder().
                path("processes").
                path(process.getId()).
                toString();
    }

    public String getPath() {
        return uriInfo.getBaseUriBuilder().
                path("processes").
                path(process.getId()).
                path("answers").
                toString();
    }
}
