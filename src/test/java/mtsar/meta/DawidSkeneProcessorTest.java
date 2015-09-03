package mtsar.meta;

import com.google.common.collect.Lists;
import mtsar.api.Answer;
import mtsar.api.AnswerAggregation;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.meta.DawidSkeneProcessor;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static mtsar.TestHelper.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DawidSkeneProcessorTest {
    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final AnswerDAO answerDAO = mock(AnswerDAO.class);
    private static final mtsar.api.Process process = mock(mtsar.api.Process.class);
    private static final Task task1 = fixture("task1.json", Task.class);
    private static final Task task2 = fixture("task2.json", Task.class);
    private static final AnswerAggregator aggregator = new DawidSkeneProcessor(() -> process, taskDAO, answerDAO);

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
                new Answer.Builder().setWorkerId(1).setTaskId(2).addAnswers("1").buildPartial(),
                new Answer.Builder().setWorkerId(2).setTaskId(2).addAnswers("2").buildPartial()
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
            assertThat(winner.get().getAnswers().get(0)).isEqualTo("1");
        }
    }

    @Test
    public void testEmptyCase() {
        when(answerDAO.listForProcess(anyString())).thenReturn(Collections.emptyList());
        final Optional<AnswerAggregation> winner = aggregator.aggregate(task1);
        assertThat(winner.isPresent()).isFalse();
    }
}
