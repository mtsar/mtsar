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

import com.google.common.collect.Lists;
import io.dropwizard.jersey.PATCH;
import mtsar.ParamsUtils;
import mtsar.api.*;
import mtsar.api.Process;
import mtsar.api.csv.WorkerCSV;
import mtsar.api.csv.WorkerRankingCSV;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.views.WorkersView;
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
import java.util.Map;
import java.util.Optional;

@Path("/workers")
@Produces(mtsar.MediaType.APPLICATION_JSON)
public class WorkerResource {
    protected final mtsar.api.Process process;
    protected final TaskDAO taskDAO;
    protected final WorkerDAO workerDAO;
    protected final AnswerDAO answerDAO;

    public WorkerResource(Process process, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        this.process = process;
        this.taskDAO = taskDAO;
        this.workerDAO = workerDAO;
        this.answerDAO = answerDAO;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public WorkersView getWorkersView(@Context UriInfo uriInfo) {
        return new WorkersView(uriInfo, process, workerDAO);
    }

    @GET
    public List<Worker> getWorkers() {
        return workerDAO.listForProcess(process.getId());
    }

    @GET
    @Produces(mtsar.MediaType.TEXT_CSV)
    public StreamingOutput getCSV() {
        final List<Worker> workers = workerDAO.listForProcess(process.getId());
        return output -> WorkerCSV.write(workers, output);
    }

    @GET
    @Path("rankings.csv")
    @Produces(mtsar.MediaType.TEXT_CSV)
    public StreamingOutput getWorkerRankingsCSV() {
        final List<Worker> workers = workerDAO.listForProcess(process.getId());
        final Map<Worker, WorkerRanking> rankings = process.getWorkerRanker().rank(workers);
        return output -> WorkerRankingCSV.write(rankings.values(), output);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postWorkers(@Context UriInfo uriInfo, @FormDataParam("file") InputStream stream) throws IOException {
        try (final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            try (final CSVParser csv = new CSVParser(reader, WorkerCSV.FORMAT)) {
                workerDAO.insert(WorkerCSV.parse(process, csv));
            }
        }
        workerDAO.resetSequence();
        return Response.seeOther(getWorkersURI(uriInfo)).build();
    }

    @POST
    public Response postWorker(@Context UriInfo uriInfo, MultivaluedMap<String, String> params) {
        final List<String> tags = ParamsUtils.extract(params, "tags");

        int workerId = workerDAO.insert(new Worker.Builder().
                setProcess(process.getId()).
                addAllTags(tags).
                build());
        final Worker worker = workerDAO.find(workerId, process.getId());
        return Response.created(getWorkerURI(uriInfo, worker)).entity(worker).build();
    }

    @GET
    @Path("{worker}")
    public Worker getWorker(@PathParam("worker") Integer id) {
        return fetchWorker(id);
    }

    @GET
    @Path("tagged/{tag}")
    public Worker getWorkerByTag(@PathParam("tag") String tag) {
        final Worker worker = workerDAO.findByTags(process.getId(), Lists.newArrayList(tag));
        if (worker == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
        return worker;
    }

    @GET
    @Path("{worker}/task")
    public TaskAllocation getWorkerTask(@PathParam("worker") Integer id) {
        final Worker worker = fetchWorker(id);
        final Optional<TaskAllocation> allocation = process.getTaskAllocator().allocate(worker);
        return allocation.isPresent() ? allocation.get() : null;
    }

    @GET
    @Path("{worker}/task/{task}")
    public TaskAllocation getWorkerTaskAgain(@PathParam("worker") Integer id, @PathParam("task") Integer taskId) {
        final Worker worker = fetchWorker(id);
        final Task task = fetchTask(taskId);
        final Optional<TaskAllocation> optional = process.getTaskAllocator().allocate(worker);

        if (optional.isPresent()) {
            return new TaskAllocation.Builder().mergeFrom(optional.get()).build();
        } else {
            return new TaskAllocation.Builder().
                    setWorker(worker).
                    setTask(task).
                    setTaskRemaining(1).
                    setTaskCount(taskDAO.count(process.getId())).
                    build();
        }
    }

    @GET
    @Path("{worker}/tasks/{n}")
    public List<TaskAllocation> getWorkerTasks(@PathParam("worker") Integer id, @PathParam("n") Integer n) {
        final Worker worker = fetchWorker(id);
        return process.getTaskAllocator().allocate(worker, n);
    }

    @GET
    @Path("{worker}/answers")
    public List<Answer> getWorkerAnswers(@PathParam("worker") Integer id) {
        final Worker worker = fetchWorker(id);
        return answerDAO.listForWorker(worker.getId(), process.getId());
    }

    @PATCH
    @Path("{worker}")
    public Worker patchWorker(@PathParam("worker") Integer id) {
        final Worker worker = fetchWorker(id);
        throw new WebApplicationException(Response.Status.NOT_IMPLEMENTED);
    }

    @DELETE
    @Path("{worker}")
    public Worker deleteWorker(@PathParam("worker") Integer id) {
        final Worker worker = fetchWorker(id);
        workerDAO.delete(id, process.getId());
        return worker;
    }

    @DELETE
    public void deleteTasks() {
        workerDAO.deleteAll(process.getId());
        workerDAO.resetSequence();
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

    private URI getWorkersURI(UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().
                path("processes").path(process.getId()).
                path("workers").
                build();
    }

    private URI getWorkerURI(UriInfo uriInfo, Worker worker) {
        return uriInfo.getBaseUriBuilder().
                path("processes").path(process.getId()).
                path("workers").path(worker.getId().toString()).
                build();
    }
}
