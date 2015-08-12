package mtsar.views;

import io.dropwizard.views.View;
import mtsar.api.Process;
import mtsar.api.sql.TaskDAO;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

public class TasksView extends View {
    private final UriInfo uriInfo;
    private final Process process;
    private final TaskDAO taskDAO;

    @Inject
    public TasksView(UriInfo uriInfo, Process process, TaskDAO taskDAO) {
        super("tasks.mustache");
        this.uriInfo = uriInfo;
        this.process = process;
        this.taskDAO = taskDAO;
    }

    public String getTitle() {
        return String.format("Tasks of \"%s\"", process.getId());
    }

    public Process getProcess() {
        return process;
    }

    public int getTaskCount() {
        return taskDAO.count(process.getId());
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
                path("tasks").
                toString();
    }
}
