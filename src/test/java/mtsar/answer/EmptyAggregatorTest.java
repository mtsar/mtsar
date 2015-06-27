package mtsar.answer;

import mtsar.api.AnswerAggregation;
import mtsar.api.Task;
import mtsar.processors.answer.EmptyAggregator;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class EmptyAggregatorTest {
    private static final Task task = mock(Task.class);
    private static final EmptyAggregator emptyAggregator = new EmptyAggregator();

    @Test
    public void testEmptyCase() {
        final Optional<AnswerAggregation> winner = emptyAggregator.aggregate(task);
        assertThat(winner.isPresent()).isFalse();
    }
}
