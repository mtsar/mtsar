package mtsar.task;

import com.google.common.collect.Lists;
import mtsar.api.Answer;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.api.jdbi.AnswerDAO;
import mtsar.api.jdbi.TaskDAO;
import mtsar.processors.task.InverseCountAllocator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class InverseCountAllocatorTest {
    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final AnswerDAO answerDAO = mock(AnswerDAO.class);
    private static final Process process = mock(Process.class);
    private static final Worker worker = mock(Worker.class);
    private static final List<Task> tasks = Lists.newArrayList(mock(Task.class), mock(Task.class));
    private static final List<Answer> answers1 = Lists.newArrayList(mock(Answer.class));
    private static final List<Answer> answers2 = Lists.newArrayList(mock(Answer.class), mock(Answer.class));
    private static final InverseCountAllocator inverseCountAllocator = new InverseCountAllocator(Process.wrap(process), taskDAO, answerDAO);

    @Before
    public void setup() {
        when(process.getId()).thenReturn("1");
        when(tasks.get(0).getId()).thenReturn(1);
        when(tasks.get(1).getId()).thenReturn(2);
    }

    @Test
    public void testUnequalAllocation() {
        reset(taskDAO);
        reset(answerDAO);
        when(taskDAO.listForProcess(anyString())).thenReturn(tasks);
        when(answerDAO.listForTask(eq(1), anyString())).thenReturn(answers1);
        when(answerDAO.listForTask(eq(2), anyString())).thenReturn(answers2);
        final Optional<Task> task = inverseCountAllocator.allocate(worker);
        assertThat(task.isPresent()).isTrue();
        assertThat(task.get()).isEqualTo(tasks.get(0));
    }

    @Test
    public void testEqualAllocation() {
        reset(taskDAO);
        reset(answerDAO);
        when(taskDAO.listForProcess(anyString())).thenReturn(tasks);
        when(answerDAO.listForTask(eq(1), anyString())).thenReturn(answers1);
        when(answerDAO.listForTask(eq(2), anyString())).thenReturn(answers1);
        final Optional<Task> task = inverseCountAllocator.allocate(worker);
        assertThat(task.isPresent()).isTrue();
        assertThat(task.get()).isIn(tasks);
    }

    @Test
    public void testEmpty() {
        reset(taskDAO);
        reset(answerDAO);
        final Optional<Task> task = inverseCountAllocator.allocate(worker);
        assertThat(task.isPresent()).isFalse();
    }
}
