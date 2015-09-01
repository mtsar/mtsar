package mtsar.answer;

import com.google.common.collect.Lists;
import mtsar.api.Answer;
import mtsar.api.AnswerAggregation;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import mtsar.processors.answer.MajorityVoting;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class MajorityVotingTest {
    private static final AnswerDAO answerDAO = mock(AnswerDAO.class);
    private static final Process process = mock(Process.class);
    private static final Task task = mock(Task.class);
    private static final MajorityVoting aggregator = new MajorityVoting(() -> process, answerDAO);

    @Before
    public void setup() {
        when(process.getId()).thenReturn("1");
        when(task.getId()).thenReturn(1);
    }

    @Test
    public void testBasicCase() {
        reset(answerDAO);
        when(answerDAO.listForTask(eq(1), anyString())).thenReturn(Lists.newArrayList(
                new Answer.Builder().addAnswers("1").buildPartial(),
                new Answer.Builder().addAnswers("1").buildPartial(),
                new Answer.Builder().addAnswers("2").buildPartial(),
                new Answer.Builder().addAnswers("3").buildPartial()
        ));
        assertThatThrownBy(() -> {
            final Optional<AnswerAggregation> winner = aggregator.aggregate(task);
            assertThat(winner.isPresent()).isTrue();
            final Answer answer = winner.get().getAnswer();
            assertThat(answer.getAnswer()).isEqualTo("1");
        }).isInstanceOf(UnsupportedOperationException.class).hasMessageContaining("Not Implemented Yet");
    }

    @Test
    public void testAmbiguousCase() {
        reset(answerDAO);
        when(answerDAO.listForTask(eq(1), anyString())).thenReturn(Lists.newArrayList(
                new Answer.Builder().addAnswers("2").buildPartial(),
                new Answer.Builder().addAnswers("1").buildPartial()
        ));
        assertThatThrownBy(() -> {
            final Optional<AnswerAggregation> winner = aggregator.aggregate(task);
            assertThat(winner.isPresent()).isTrue();
            final Answer answer = winner.get().getAnswer();
            assertThat(answer.getAnswer()).isEqualTo("1");
        }).isInstanceOf(UnsupportedOperationException.class).hasMessageContaining("Not Implemented Yet");
    }

    @Test
    public void testEmptyCase() {
        reset(answerDAO);
        when(answerDAO.listForTask(eq(1), anyString())).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> {
            final Optional<AnswerAggregation> winner = aggregator.aggregate(task);
            assertThat(winner.isPresent()).isFalse();
        }).isInstanceOf(UnsupportedOperationException.class).hasMessageContaining("Not Implemented Yet");
    }
}
