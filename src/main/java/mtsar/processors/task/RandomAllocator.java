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

package mtsar.processors.task;

import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.TaskAllocation;
import mtsar.api.Worker;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.TaskAllocator;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RandomAllocator implements TaskAllocator {
    protected final Provider<Process> process;
    protected final TaskDAO taskDAO;

    @Inject
    public RandomAllocator(Provider<Process> processProvider, TaskDAO taskDAO) {
        this.process = processProvider;
        this.taskDAO = taskDAO;
    }

    @Override
    @Nonnull
    public List<TaskAllocation> allocate(@Nonnull Worker worker, @Nonnegative int n) {
        final List<Task> tasks = taskDAO.listForProcess(process.get().getId());
        Collections.shuffle(tasks);
        return tasks.stream().limit(n).
                map(task -> new TaskAllocation.Builder()
                        .setWorker(worker)
                        .setTask(task)
                        .setTaskRemaining(tasks.size())
                        .setTaskCount(tasks.size())
                        .build()).
                collect(Collectors.toList());
    }
}
