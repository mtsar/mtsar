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

import com.google.common.base.Function;
import io.dropwizard.views.View;
import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class ProcessesView extends View {
    private final Map<String, Process> processes;
    private final TaskDAO taskDAO;
    private final WorkerDAO workerDAO;
    private final AnswerDAO answerDAO;

    @Inject
    public ProcessesView(Map<String, Process> processes, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        super("processes.mustache");
        this.processes = requireNonNull(processes);
        this.taskDAO = requireNonNull(taskDAO);
        this.workerDAO = requireNonNull(workerDAO);
        this.answerDAO = requireNonNull(answerDAO);
    }

    @SuppressWarnings("unused")
    public String getTitle() {
        return "Processes";
    }

    @SuppressWarnings("unused")
    public Collection<Process> getProcesses() {
        return processes.values();
    }

    @SuppressWarnings("unused")
    public Function<String, Integer> getWorkerCount() {
        return id -> workerDAO.count(id);
    }

    @SuppressWarnings("unused")
    public Function<String, Integer> getTaskCount() {
        return id -> taskDAO.count(id);
    }

    @SuppressWarnings("unused")
    public Function<String, Integer> getAnswerCount() {
        return id -> answerDAO.count(id);
    }
}
