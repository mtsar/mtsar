package mtsar.task;

import com.google.common.collect.Lists;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.TaskAllocation;
import mtsar.api.Worker;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.task.RandomAllocator;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class RandomAllocatorTest {
    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final Process process = mock(Process.class);
    private static final Worker worker = new Worker.Builder().setId(1).setProcess("1").build();
    private static final Task task1 = new Task.Builder().
            setId(1).setProcess("1").setDescription("").setType(TaskDAO.TASK_TYPE_SINGLE).addAnswers("1", "2").
            build();
    private static final Task task2 = new Task.Builder().
            setId(2).setProcess("1").setDescription("").setType(TaskDAO.TASK_TYPE_SINGLE).addAnswers("a", "b").
            build();
    private static final List<Task> tasks = Lists.newArrayList(task1, task2);
    private static final RandomAllocator allocator = new RandomAllocator(() -> process, taskDAO);

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
