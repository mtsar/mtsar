package mtsar.answer;

import com.google.common.collect.Lists;
import mtsar.api.Answer;
import mtsar.api.AnswerAggregation;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.answer.KOSAggregator;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class KOSAggregatorTest {
    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final AnswerDAO answerDAO = mock(AnswerDAO.class);
    private static final mtsar.api.Process process = mock(Process.class);
    private static final Task task1 = new Task.Builder().
            setId(1).setProcess("1").setDescription("").setType(TaskDAO.TASK_TYPE_SINGLE).addAnswers("1", "2").
            build();
    private static final Task task2 = new Task.Builder().
            setId(2).setProcess("1").setDescription("").setType(TaskDAO.TASK_TYPE_SINGLE).addAnswers("a", "b").
            build();
    private static final KOSAggregator aggregator = new KOSAggregator(() -> process, taskDAO, answerDAO);

    @Before
    public void setup() {
        reset(taskDAO);
        reset(answerDAO);
        when(process.getId()).thenReturn("1");
    }

    @Test
    public void testTwoTasks() {
        when(taskDAO.listForProcess(anyString())).thenReturn(Lists.newArrayList(task1, task2));
        when(answerDAO.listForProcess(anyString())).thenReturn(Lists.newArrayList(
                new Answer.Builder().setWorkerId(1).setTaskId(1).addAnswers("1").buildPartial(),
                new Answer.Builder().setWorkerId(2).setTaskId(1).addAnswers("1").buildPartial(),
                new Answer.Builder().setWorkerId(3).setTaskId(1).addAnswers("2").buildPartial(),
                new Answer.Builder().setWorkerId(1).setTaskId(2).addAnswers("a").buildPartial(),
                new Answer.Builder().setWorkerId(2).setTaskId(2).addAnswers("b").buildPartial(),
                new Answer.Builder().setWorkerId(3).setTaskId(2).addAnswers("b").buildPartial()
        ));
        {
            final Optional<AnswerAggregation> winner = aggregator.aggregate(task1);
            assertThat(winner.isPresent());
            assertThat(winner.get().getAnswers()).hasSize(1);
            assertThat(winner.get().getAnswers().get(0)).isEqualTo("1");
        }
        {
            final Optional<AnswerAggregation> winner = aggregator.aggregate(task2);
            assertThat(winner.isPresent());
            assertThat(winner.get().getAnswers()).hasSize(1);
            assertThat(winner.get().getAnswers().get(0)).isEqualTo("b");
        }
    }

    @Test
    public void testEmptyCase() {
        when(answerDAO.listForProcess(anyString())).thenReturn(Collections.emptyList());
        final Optional<AnswerAggregation> winner = aggregator.aggregate(task1);
        assertThat(winner.isPresent()).isFalse();
    }
}
