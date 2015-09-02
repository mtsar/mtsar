package mtsar.answer;

import com.google.common.collect.Lists;
import mtsar.api.Answer;
import mtsar.api.AnswerAggregation;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import mtsar.processors.answer.RandomAggregator;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static mtsar.TestHelper.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class RandomAggregatorTest {
    private static final AnswerDAO answerDAO = mock(AnswerDAO.class);
    private static final Process process = mock(Process.class);
    private static final Task task = fixture("task1.json", Task.class);
    private static final RandomAggregator aggregator = new RandomAggregator(() -> process, answerDAO);

    @Before
    public void setup() {
        reset(answerDAO);
        when(process.getId()).thenReturn("1");
    }

    @Test
    public void testBasicCase() {
        when(answerDAO.listForTask(eq(1), anyString())).thenReturn(Lists.newArrayList(
                new Answer.Builder().addAnswers("1").buildPartial(),
                new Answer.Builder().addAnswers("2").buildPartial(),
                new Answer.Builder().addAnswers("3").buildPartial()
        ));
        final Optional<AnswerAggregation> winner = aggregator.aggregate(task);
        assertThat(winner.isPresent());
        assertThat(winner.get().getAnswers()).hasSize(1);
        assertThat(winner.get().getAnswers().get(0)).isIn("1", "2", "3");
    }

    @Test
    public void testEmptyCase() {
        when(answerDAO.listForTask(any(), anyString())).thenReturn(Collections.emptyList());
        final Optional<AnswerAggregation> winner = aggregator.aggregate(task);
        assertThat(winner.isPresent()).isFalse();
    }
}
