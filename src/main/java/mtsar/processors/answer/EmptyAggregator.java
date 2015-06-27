package mtsar.processors.answer;

import mtsar.api.AnswerAggregation;
import mtsar.api.Task;
import mtsar.processors.AnswerAggregator;

import java.util.Optional;

public class EmptyAggregator implements AnswerAggregator {
    @Override
    public Optional<AnswerAggregation> aggregate(Task task) {
        return Optional.empty();
    }
}
