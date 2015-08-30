package mtsar.task;

import com.google.common.collect.Lists;
import mtsar.api.*;
import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.task.InverseCountAllocator;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class InverseCountAllocatorTest {
    private static final Process process = mock(Process.class);
    private static final Worker worker = mock(Worker.class);

    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final Task task1 = mock(Task.class), task2 = mock(Task.class);
    private static final List<Task> tasks = Lists.newArrayList(task1, task2);

    private static final AnswerDAO answerDAO = mock(AnswerDAO.class);
    private static final Answer answer1 = mock(Answer.class), answer2 = mock(Answer.class), answer3 = mock(Answer.class);
    private static final List<Answer> answers1 = Lists.newArrayList(answer1);
    private static final List<Answer> answers2 = Lists.newArrayList(answer2, answer3);

    private static final DBI dbi = mock(DBI.class);
    private static final InverseCountAllocator.CountDAO countDAO = mock(InverseCountAllocator.CountDAO.class);

    @Before
    public void setup() {
        reset(taskDAO);
        reset(answerDAO);
        reset(countDAO);
        when(taskDAO.select(eq(Lists.newArrayList(1)), anyString())).thenReturn(Lists.newArrayList(task1));
        when(taskDAO.select(eq(Lists.newArrayList(2)), anyString())).thenReturn(Lists.newArrayList(task2));
        when(taskDAO.count(anyString())).thenReturn(tasks.size());
        when(answerDAO.listForWorker(anyInt(), anyString())).thenReturn(Collections.emptyList());
        when(dbi.onDemand(any())).thenReturn(countDAO);
        when(process.getId()).thenReturn("1");
        when(task1.getId()).thenReturn(1);
        when(task2.getId()).thenReturn(2);
        when(answer1.getTaskId()).thenReturn(1);
        when(answer2.getTaskId()).thenReturn(2);
        when(answer3.getTaskId()).thenReturn(2);
    }

    @Test
    public void testUnequalAllocation() {
        when(countDAO.getCountsSQL(anyString())).thenReturn(Lists.newArrayList(Pair.of(1, 1), Pair.of(2, 0)));
        final InverseCountAllocator allocator = new InverseCountAllocator(() -> process, dbi, taskDAO, answerDAO);

        final Optional<TaskAllocation> optional = allocator.allocate(worker);
        assertThat(optional.isPresent()).isTrue();

        final TaskAllocation allocation = optional.get();
        assertThat(allocation.getTask().getId()).isEqualTo(2);
        assertThat(allocation.getTaskRemaining()).isEqualTo(2);
        assertThat(allocation.getTaskCount()).isEqualTo(2);
    }

    @Test
    public void testEqualAllocation() {
        when(countDAO.getCountsSQL(anyString())).thenReturn(Lists.newArrayList(Pair.of(1, 0), Pair.of(2, 0)));
        final InverseCountAllocator allocator = new InverseCountAllocator(() -> process, dbi, taskDAO, answerDAO);

        final Optional<TaskAllocation> optional = allocator.allocate(worker);
        assertThat(optional.isPresent()).isTrue();

        final TaskAllocation allocation = optional.get();
        assertThat(allocation.getTask().getId()).isBetween(1, 2);
        assertThat(allocation.getTaskRemaining()).isEqualTo(2);
        assertThat(allocation.getTaskCount()).isEqualTo(2);
    }

    @Test
    public void testEmpty() {
        when(countDAO.getCountsSQL(anyString())).thenReturn(Collections.emptyList());
        final InverseCountAllocator allocator = new InverseCountAllocator(() -> process, dbi, taskDAO, answerDAO);
        final Optional<TaskAllocation> optional = allocator.allocate(worker);
        assertThat(optional.isPresent()).isFalse();
    }
}
