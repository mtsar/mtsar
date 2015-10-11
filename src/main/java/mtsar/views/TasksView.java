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
import mtsar.api.sql.TaskDAO;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import static java.util.Objects.requireNonNull;

public class TasksView extends View {
    private final UriInfo uriInfo;
    private final Stage stage;
    private final TaskDAO taskDAO;

    @Inject
    public TasksView(UriInfo uriInfo, Stage stage, TaskDAO taskDAO) {
        super("tasks.mustache");
        this.uriInfo = requireNonNull(uriInfo);
        this.stage = requireNonNull(stage);
        this.taskDAO = requireNonNull(taskDAO);
    }

    @SuppressWarnings("unused")
    public String getTitle() {
        return String.format("Tasks of \"%s\"", stage.getId());
    }

    @SuppressWarnings("unused")
    public Stage getStage() {
        return stage;
    }

    @SuppressWarnings("unused")
    public int getTaskCount() {
        return taskDAO.count(stage.getId());
    }

    @SuppressWarnings("unused")
    public String getStagePath() {
        return uriInfo.getBaseUriBuilder().
                path("processes").
                path(stage.getId()).
                toString();
    }

    @SuppressWarnings("unused")
    public String getPath() {
        return uriInfo.getBaseUriBuilder().
                path("processes").
                path(stage.getId()).
                path("tasks").
                toString();
    }
}
