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
import mtsar.MechanicalTsarVersion;
import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;

import javax.inject.Inject;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class DashboardView extends View {
    private final MechanicalTsarVersion version;
    private final Map<String, Process> processes;
    private final TaskDAO taskDAO;
    private final WorkerDAO workerDAO;
    private final AnswerDAO answerDAO;

    @Inject
    public DashboardView(MechanicalTsarVersion version, Map<String, Process> processes, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        super("dashboard.mustache");
        checkNotNull(this.version = version);
        checkNotNull(this.processes = processes);
        checkNotNull(this.taskDAO = taskDAO);
        checkNotNull(this.workerDAO = workerDAO);
        checkNotNull(this.answerDAO = answerDAO);
    }

    @SuppressWarnings("unused")
    public String getTitle() {
        return "Dashboard";
    }

    @SuppressWarnings("unused")
    public String getVersion() {
        return version.getVersion();
    }

    @SuppressWarnings("unused")
    public String getJvm() {
        return System.getProperty("java.runtime.version");
    }

    @SuppressWarnings("unused")
    public int getProcessCount() {
        return processes.size();
    }

    @SuppressWarnings("unused")
    public int getWorkerCount() {
        return processes.values().stream().
                map(process -> workerDAO.count(process.getId())).
                reduce(0, (r, e) -> r + e);
    }

    @SuppressWarnings("unused")
    public int getTaskCount() {
        return processes.values().stream().
                map(process -> taskDAO.count(process.getId())).
                reduce(0, (r, e) -> r + e);
    }

    @SuppressWarnings("unused")
    public int getAnswerCount() {
        return processes.values().stream().
                map(process -> answerDAO.count(process.getId())).
                reduce(0, (r, e) -> r + e);
    }
}
