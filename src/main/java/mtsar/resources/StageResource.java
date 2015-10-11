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

import mtsar.api.Answer;
import mtsar.api.Stage;
import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.api.csv.AnswerCSV;
import mtsar.api.csv.TaskCSV;
import mtsar.api.csv.WorkerCSV;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.views.StageView;
import mtsar.views.StagesView;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Singleton
@Path("/processes")
@Produces(mtsar.MediaType.APPLICATION_JSON)
public class StageResource {
    protected final Map<String, Stage> stages;
    protected final TaskDAO taskDAO;
    protected final WorkerDAO workerDAO;
    protected final AnswerDAO answerDAO;

    @Inject
    public StageResource(@Named("stages") Map<String, Stage> stages, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        this.stages = stages;
        this.taskDAO = taskDAO;
        this.workerDAO = workerDAO;
        this.answerDAO = answerDAO;
    }

    @GET
    public Collection<Stage> getStages() {
        return stages.values();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public StagesView getStagesView() {
        return new StagesView(stages, taskDAO, workerDAO, answerDAO);
    }

    @GET
    @Path("{stage}")
    public Stage getStage(@PathParam("stage") String id) {
        return fetchStage(id);
    }

    @GET
    @Path("{stage}")
    @Produces(MediaType.TEXT_HTML)
    public StageView getStageView(@PathParam("stage") String id) {
        return new StageView(fetchStage(id), taskDAO, workerDAO, answerDAO);
    }

    @Path("{stage}/workers")
    public WorkerResource getWorkers(@PathParam("stage") String id) {
        return new WorkerResource(fetchStage(id), taskDAO, workerDAO, answerDAO);
    }

    @GET
    @Path("{stage}/workers.csv")
    @Produces(mtsar.MediaType.TEXT_CSV)
    public StreamingOutput getWorkersCSV(@PathParam("stage") String id) {
        final List<Worker> workers = workerDAO.listForStage(fetchStage(id).getId());
        return output -> WorkerCSV.write(workers, output);
    }

    @Path("{stage}/tasks")
    public TaskResource getTasks(@PathParam("stage") String id) {
        return new TaskResource(fetchStage(id), taskDAO, workerDAO, answerDAO);
    }

    @GET
    @Path("{stage}/tasks.csv")
    @Produces(mtsar.MediaType.TEXT_CSV)
    public StreamingOutput getTasksCSV(@PathParam("stage") String id) {
        final List<Task> tasks = taskDAO.listForStage(fetchStage(id).getId());
        return output -> TaskCSV.write(tasks, output);
    }

    @Path("{stage}/answers")
    public AnswerResource getAnswers(@PathParam("stage") String id) {
        return new AnswerResource(fetchStage(id), taskDAO, workerDAO, answerDAO);
    }

    @GET
    @Path("{stage}/answers.csv")
    @Produces(mtsar.MediaType.TEXT_CSV)
    public StreamingOutput getAnswersCSV(@PathParam("stage") String id) {
        final List<Answer> answers = answerDAO.listForStage(fetchStage(id).getId());
        return output -> AnswerCSV.write(answers, output);
    }

    protected Stage fetchStage(String id) {
        if (!stages.containsKey(id)) throw new WebApplicationException(Response.Status.NOT_FOUND);
        return stages.get(id);
    }
}
