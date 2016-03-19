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

package mtsar.views;

import io.dropwizard.views.View;
import mtsar.api.Stage;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class StagesView extends View {
    private final Map<String, Stage> stages;
    private final TaskDAO taskDAO;
    private final WorkerDAO workerDAO;
    private final AnswerDAO answerDAO;

    @Inject
    public StagesView(Map<String, Stage> stages, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        super("stages.mustache");
        this.stages = requireNonNull(stages);
        this.taskDAO = requireNonNull(taskDAO);
        this.workerDAO = requireNonNull(workerDAO);
        this.answerDAO = requireNonNull(answerDAO);
    }

    @SuppressWarnings({"unused", "SameReturnValue"})
    public String getTitle() {
        return "Stages";
    }

    @SuppressWarnings("unused")
    public Collection<Stage> getStages() {
        return stages.values();
    }

    @SuppressWarnings("unused")
    public Function<String, Integer> getWorkerCount() {
        return workerDAO::count;
    }

    @SuppressWarnings("unused")
    public Function<String, Integer> getTaskCount() {
        return taskDAO::count;
    }

    @SuppressWarnings("unused")
    public Function<String, Integer> getAnswerCount() {
        return answerDAO::count;
    }
}
