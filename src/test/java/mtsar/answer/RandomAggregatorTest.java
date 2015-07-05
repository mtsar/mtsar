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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class RandomAggregatorTest {
    private static final AnswerDAO answerDAO = mock(AnswerDAO.class);
    private static final Process process = mock(Process.class);
    private static final Task task = mock(Task.class);
    private static final RandomAggregator randomAggregator = new RandomAggregator(Process.wrap(process), answerDAO);

    @Before
    public void setup() {
        when(process.getId()).thenReturn("1");
        when(task.getId()).thenReturn(1);
    }

    @Test
    public void testBasicCase() {
        reset(answerDAO);
        when(answerDAO.listForTask(eq(1), anyString())).thenReturn(Lists.newArrayList(
                Answer.builder().setAnswer("1").build(),
                Answer.builder().setAnswer("2").build(),
                Answer.builder().setAnswer("3").build()
        ));
        final Optional<AnswerAggregation> winner = randomAggregator.aggregate(task);
        assertThat(winner.isPresent());
        final Answer answer = winner.get().getAnswer();
        assertThat(answer.getAnswers()).hasSize(1);
        assertThat(answer.getAnswer().get()).isIn("1", "2", "3");
    }

    @Test
    public void testEmptyCase() {
        reset(answerDAO);
        when(answerDAO.listForTask(eq(1), anyString())).thenReturn(Collections.emptyList());
        final Optional<AnswerAggregation> winner = randomAggregator.aggregate(task);
        assertThat(winner.isPresent()).isFalse();
    }
}
