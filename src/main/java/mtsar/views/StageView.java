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

import static java.util.Objects.requireNonNull;

public class StageView extends View {
    private final Stage stage;
    private final TaskDAO taskDAO;
    private final WorkerDAO workerDAO;
    private final AnswerDAO answerDAO;

    @Inject
    public StageView(Stage stage, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        super("stage.mustache");
        this.stage = requireNonNull(stage);
        this.taskDAO = requireNonNull(taskDAO);
        this.workerDAO = requireNonNull(workerDAO);
        this.answerDAO = requireNonNull(answerDAO);
    }

    @SuppressWarnings("unused")
    public String getTitle() {
        return String.format("Stage \"%s\"", stage.getId());
    }

    @SuppressWarnings("unused")
    public Stage getStage() {
        return stage;
    }

    /**
     * By some strange reason, mustache can not access the options map,
     * but the present method works just fine.
     *
     * @return stage options
     */
    @SuppressWarnings("unused")
    public Collection<Map.Entry<String, String>> getOptions() {
        return stage.getOptions().entrySet();
    }

    @SuppressWarnings("unused")
    public int getWorkerCount() {
        return workerDAO.count(stage.getId());
    }

    @SuppressWarnings("unused")
    public int getTaskCount() {
        return taskDAO.count(stage.getId());
    }

    @SuppressWarnings("unused")
    public int getAnswerCount() {
        return answerDAO.count(stage.getId());
    }
}
