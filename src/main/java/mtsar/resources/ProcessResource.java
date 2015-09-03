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

import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.views.ProcessView;
import mtsar.views.ProcessesView;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.util.Collection;
import java.util.Map;

@Singleton
@Path("/processes")
@Produces(mtsar.MediaType.APPLICATION_JSON)
public class ProcessResource {
    protected final Map<String, mtsar.api.Process> processes;
    protected final TaskDAO taskDAO;
    protected final WorkerDAO workerDAO;
    protected final AnswerDAO answerDAO;

    @Inject
    public ProcessResource(@Named("processes") Map<String, Process> processes, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        this.processes = processes;
        this.taskDAO = taskDAO;
        this.workerDAO = workerDAO;
        this.answerDAO = answerDAO;
    }

    @GET
    public Collection<Process> getProcesses() {
        return processes.values();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public ProcessesView getProcessView() {
        return new ProcessesView(processes, taskDAO, workerDAO, answerDAO);
    }

    @GET
    @Path("{process}")
    public Process getProcess(@PathParam("process") String id) {
        return fetchProcess(id);
    }

    @GET
    @Path("{process}")
    @Produces(MediaType.TEXT_HTML)
    public ProcessView getProcessView(@PathParam("process") String id) {
        return new ProcessView(fetchProcess(id), taskDAO, workerDAO, answerDAO);
    }

    @Path("{process}/workers")
    public WorkerResource getWorkers(@PathParam("process") String id) {
        return new WorkerResource(fetchProcess(id), taskDAO, workerDAO, answerDAO);
    }

    /*
     * TODO: how to implement this route correctly?
     */
    @GET
    @Path("{process}/workers.csv")
    @Produces(mtsar.MediaType.TEXT_CSV)
    public StreamingOutput getWorkersCSV(@PathParam("process") String id) {
        return getWorkers(id).getCSV();
    }

    @Path("{process}/tasks")
    public TaskResource getTasks(@PathParam("process") String id) {
        return new TaskResource(fetchProcess(id), taskDAO, workerDAO, answerDAO);
    }

    /*
     * TODO: how to implement this route correctly?
     */
    @GET
    @Path("{process}/tasks.csv")
    @Produces(mtsar.MediaType.TEXT_CSV)
    public StreamingOutput getTasksCSV(@PathParam("process") String id) {
        return getTasks(id).getCSV();
    }

    @Path("{process}/answers")
    public AnswerResource getAnswers(@PathParam("process") String id) {
        return new AnswerResource(fetchProcess(id), taskDAO, workerDAO, answerDAO);
    }

    /*
     * TODO: how to implement this route correctly?
     */
    @GET
    @Path("{process}/answers.csv")
    @Produces(mtsar.MediaType.TEXT_CSV)
    public StreamingOutput getAnswersCSV(@PathParam("process") String id) {
        return getAnswers(id).getCSV();
    }

    protected Process fetchProcess(String id) {
        if (!processes.containsKey(id)) throw new WebApplicationException(Response.Status.NOT_FOUND);
        return processes.get(id);
    }
}
