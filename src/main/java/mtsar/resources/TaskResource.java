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
import mtsar.DefaultDateTime;
import mtsar.ParamsUtils;
import mtsar.api.*;
import mtsar.api.Process;
import mtsar.api.csv.TaskCSV;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.api.validation.AnswerValidation;
import mtsar.api.validation.TaskAnswerValidation;
import mtsar.views.TasksView;
import org.apache.commons.csv.CSVParser;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Path("/tasks")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(mtsar.MediaType.APPLICATION_JSON)
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
    @Produces(MediaType.TEXT_HTML)
    public TasksView getTasksView(@Context UriInfo uriInfo) {
        return new TasksView(uriInfo, process, taskDAO);
    }

    @GET
    public List<Task> getTasks(@QueryParam("page") @DefaultValue("0") int page) {
        return taskDAO.listForProcess(process.getId());
    }

    @GET
    @Produces(mtsar.MediaType.TEXT_CSV)
    public StreamingOutput getCSV() {
        final List<Task> tasks = taskDAO.listForProcess(process.getId());
        return output -> TaskCSV.write(tasks, output);
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
                setProcess(process.getId()).
                build());
        final Task task = taskDAO.find(taskId, process.getId());
        return Response.created(getTaskURI(uriInfo, task)).entity(task).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postTasks(@Context UriInfo uriInfo, @FormDataParam("file") InputStream stream) throws IOException {
        try (final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            try (final CSVParser csv = new CSVParser(reader, TaskCSV.FORMAT)) {
                taskDAO.insert(TaskCSV.parse(process, csv));
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
        return answerDAO.listForTask(id, process.getId());
    }

    @POST
    @Path("{task}/answers")
    public Response postTaskAnswer(@Context Validator validator, @Context UriInfo uriInfo, @PathParam("task") Integer id, @FormParam("type") @DefaultValue("answer") String type, @FormParam("worker_id") Integer workerId, @FormParam("datetime") String datetimeParam, MultivaluedMap<String, String> params) {
        final Timestamp datetime = (datetimeParam == null) ? DefaultDateTime.get() : Timestamp.valueOf(datetimeParam);
        final Worker worker = fetchWorker(workerId);
        final Task task = fetchTask(id);
        final List<String> tags = ParamsUtils.extract(params, "tags");
        final List<String> answers = ParamsUtils.extract(params, "answers");

        final Answer record = new Answer.Builder().
                setProcess(process.getId()).
                addAllTags(tags).
                setType(type).
                setTaskId(task.getId()).
                setWorkerId(worker.getId()).
                addAllAnswers(answers).
                setDateTime(datetime).
                build();

        ParamsUtils.validate(validator,
                new TaskAnswerValidation.Builder().setTask(task).setAnswer(record).build(),
                new AnswerValidation.Builder().setAnswer(record).setAnswerDAO(answerDAO).build()
        );

        int answerId = answerDAO.insert(record);
        final Answer answer = answerDAO.find(answerId, process.getId());
        return Response.created(getAnswerURI(uriInfo, answer)).entity(answer).build();
    }

    @GET
    @Path("{task}/answer")
    public AnswerAggregation getTaskAnswer(@PathParam("task") Integer id) {
        final Task task = fetchTask(id);
        final Optional<AnswerAggregation> aggregation = process.getAnswerAggregator().aggregate(task);
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
        taskDAO.delete(id, process.getId());
        return task;
    }

    @DELETE
    public void deleteTasks() {
        taskDAO.deleteAll(process.getId());
        taskDAO.resetSequence();
    }

    private Worker fetchWorker(Integer id) {
        final Worker worker = workerDAO.find(id, process.getId());
        if (worker == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
        return worker;
    }

    private Task fetchTask(Integer id) {
        final Task task = taskDAO.find(id, process.getId());
        if (task == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
        return task;
    }

    private URI getTasksURI(UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().
                path("processes").path(process.getId()).
                path("tasks").
                build();
    }

    private URI getTaskURI(UriInfo uriInfo, Task task) {
        return uriInfo.getBaseUriBuilder().
                path("processes").path(process.getId()).
                path("tasks").path(task.getId().toString()).
                build();
    }

    private URI getAnswerURI(UriInfo uriInfo, Answer answer) {
        return uriInfo.getBaseUriBuilder().
                path("processes").path(process.getId()).
                path("answers").path(answer.getId().toString()).
                build();
    }
}
