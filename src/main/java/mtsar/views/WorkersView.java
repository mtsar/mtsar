package mtsar.views;

import io.dropwizard.views.View;
import mtsar.api.Process;
import mtsar.api.sql.WorkerDAO;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

public class WorkersView extends View {
    private final UriInfo uriInfo;
    private final Process process;
    private final WorkerDAO workerDAO;

    @Inject
    public WorkersView(UriInfo uriInfo, Process process, WorkerDAO workerDAO) {
        super("workers.mustache");
        this.uriInfo = uriInfo;
        this.process = process;
        this.workerDAO = workerDAO;
    }

    public String getTitle() {
        return String.format("Workers of \"%s\"", process.getId());
    }

    public Process getProcess() {
        return process;
    }

    public int getWorkerCount() {
        return workerDAO.count(process.getId());
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
                path("workers").
                toString();
    }
}
