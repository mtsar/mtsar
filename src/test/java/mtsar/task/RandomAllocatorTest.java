package mtsar.task;

import com.google.common.collect.Lists;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.api.jdbi.TaskDAO;
import mtsar.processors.task.RandomAllocator;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class RandomAllocatorTest {
    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final Process process = mock(Process.class);
    private static final Worker worker = mock(Worker.class);
    private static final List<Task> tasks = Lists.newArrayList(mock(Task.class), mock(Task.class));
    private static final RandomAllocator randomAllocator = new RandomAllocator(taskDAO);

    @Before
    public void setup() {
        when(process.getId()).thenReturn("1");
        randomAllocator.setProcess(process);
        Collections.shuffle(tasks);
    }

    @Test
    public void testAllocation() {
        reset(taskDAO);
        when(taskDAO.random(anyString())).thenReturn(tasks.get(0));
        final Optional<Task> task = randomAllocator.allocate(worker);
        assertThat(task.isPresent()).isTrue();
        assertThat(task.get()).isIn(tasks);
    }

    @Test
    public void testEmpty() {
        reset(taskDAO);
        when(taskDAO.random(anyString())).thenReturn(null);
        final Optional<Task> task = randomAllocator.allocate(worker);
        assertThat(task.isPresent()).isFalse();
    }
}
