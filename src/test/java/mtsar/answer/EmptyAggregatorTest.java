package mtsar.answer;

import mtsar.api.AnswerAggregation;
import mtsar.api.Task;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.answer.EmptyAggregator;
import org.junit.Test;

import java.util.Optional;

import static mtsar.TestHelper.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class EmptyAggregatorTest {
    private static final Task task = fixture("task1.json", Task.class);
    private static final AnswerAggregator aggregator = new EmptyAggregator();

    @Test
    public void testEmptyCase() {
        final Optional<AnswerAggregation> winner = aggregator.aggregate(task);
        assertThat(winner.isPresent()).isFalse();
    }
}
