/*
 * Copyright 2015 Dmitry Ustalov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mtsar.resources;

import io.dropwizard.jersey.PATCH;
import mtsar.ParamsUtils;
import mtsar.api.*;
import mtsar.api.csv.TaskCSV;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.views.TasksView;
import org.apache.commons.csv.CSVParser;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Path("/tasks")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(mtsar.MediaType.APPLICATION_JSON)
public class TaskResource {
    protected final Stage stage;
    protected final TaskDAO taskDAO;
    protected final WorkerDAO workerDAO;
    protected final AnswerDAO answerDAO;

    public TaskResource(Stage stage, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        this.stage = stage;
        this.taskDAO = taskDAO;
        this.workerDAO = workerDAO;
        this.answerDAO = answerDAO;
    }

    @GET
    public List<Task> getTasks(@QueryParam("page") @DefaultValue("0") int page) {
        return taskDAO.listForStage(stage.getId());
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TasksView getTasksView(@Context UriInfo uriInfo) {
        return new TasksView(uriInfo, stage, taskDAO);
    }

    @POST
    public Response postTask(@Context UriInfo uriInfo, @FormParam("type") @DefaultValue("single") String type, @FormParam("description") String description, MultivaluedMap<String, String> params) {
        final List<String> tags = ParamsUtils.extract(params, "tags");
        final List<String> answers = ParamsUtils.extract(params, "answers");

        int taskId = taskDAO.insert(new Task.Builder().
                addAllTags(tags).
                setType(type).
                setDescription(description).
                addAllAnswers(answers).
                setStage(stage.getId()).
                build());
        final Task task = taskDAO.find(taskId, stage.getId());
        return Response.created(getTaskURI(uriInfo, task)).entity(task).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postTasksCSV(@Context UriInfo uriInfo, @FormDataParam("file") InputStream stream) throws IOException {
        try (final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            try (final CSVParser csv = new CSVParser(reader, TaskCSV.FORMAT)) {
                taskDAO.insert(TaskCSV.parse(stage, csv));
            }
        }
        taskDAO.resetSequence();
        return Response.seeOther(getTasksURI(uriInfo)).build();
    }

    @GET
    @Path("{task}")
    public Task getTask(@PathParam("task") Integer id) {
        return fetchTask(id);
    }

    @GET
    @Path("{task}/answers")
    public List<Answer> getTaskAnswers(@PathParam("task") Integer id) {
        return answerDAO.listForTask(id, stage.getId());
    }

    @GET
    @Path("{task}/answer")
    public AnswerAggregation getTaskAnswer(@PathParam("task") Integer id) {
        final Task task = fetchTask(id);
        final Optional<AnswerAggregation> aggregation = stage.getAnswerAggregator().aggregate(task);
        return aggregation.isPresent() ? aggregation.get() : null;
    }

    @PATCH
    @Path("{task}")
    public Task patchTask(@PathParam("task") Integer id) {
        final Task task = fetchTask(id);
        throw new WebApplicationException(Response.Status.NOT_IMPLEMENTED);
    }

    @DELETE
    @Path("{task}")
    public Task deleteTask(@PathParam("task") Integer id) {
        final Task task = fetchTask(id);
        taskDAO.delete(id, stage.getId());
        return task;
    }

    @DELETE
    public void deleteTasks() {
        taskDAO.deleteAll(stage.getId());
        taskDAO.resetSequence();
    }

    private Task fetchTask(Integer id) {
        final Task task = taskDAO.find(id, stage.getId());
        if (task == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
        return task;
    }

    private URI getTasksURI(UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().
                path("processes").path(stage.getId()).
                path("tasks").
                build();
    }

    private URI getTaskURI(UriInfo uriInfo, Task task) {
        return uriInfo.getBaseUriBuilder().
                path("processes").path(stage.getId()).
                path("tasks").path(task.getId().toString()).
                build();
    }
}
