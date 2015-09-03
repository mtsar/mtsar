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

package mtsar.task;

import com.google.common.collect.Lists;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.TaskAllocation;
import mtsar.api.Worker;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.TaskAllocator;
import mtsar.processors.task.RandomAllocator;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static mtsar.TestHelper.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class RandomAllocatorTest {
    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final Process process = mock(Process.class);
    private static final Worker worker = fixture("worker1.json", Worker.class);
    private static final Task task1 = fixture("task1.json", Task.class);
    private static final Task task2 = fixture("task2.json", Task.class);
    private static final List<Task> tasks = Lists.newArrayList(task1, task2);
    private static final TaskAllocator allocator = new RandomAllocator(() -> process, taskDAO);

    @Before
    public void setup() {
        reset(taskDAO);
        when(process.getId()).thenReturn("1");
        Collections.shuffle(tasks);
    }

    @Test
    public void testAllocation() {
        when(taskDAO.listForProcess(anyString())).thenReturn(tasks);
        final Optional<TaskAllocation> allocation = allocator.allocate(worker);
        assertThat(allocation.isPresent()).isTrue();
        assertThat(allocation.get().getTask()).isIn(tasks);
    }

    @Test
    public void testEmpty() {
        when(taskDAO.listForProcess(anyString())).thenReturn(Collections.emptyList());
        final Optional<TaskAllocation> allocation = allocator.allocate(worker);
        assertThat(allocation.isPresent()).isFalse();
    }
}
