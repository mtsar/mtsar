package mtsar.views;

import io.dropwizard.views.View;
import mtsar.api.Process;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

public class AnswersView extends View {
    private final UriInfo uriInfo;
    private final Process process;

    @Inject
    public AnswersView(UriInfo uriInfo, Process process) {
        super("answers.mustache");
        this.uriInfo = uriInfo;
        this.process = process;
    }

    public String getTitle() {
        return String.format("Answers of \"%s\"", process.getId());
    }

    public Process getProcess() {
        return process;
    }

    public String getPath() {
        return uriInfo.getBaseUriBuilder().
                path("processes").
                path(process.getId()).
                path("answers").
                toString();
    }
}
