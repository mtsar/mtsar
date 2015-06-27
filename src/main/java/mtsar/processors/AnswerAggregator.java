package mtsar.processors;

import mtsar.api.AnswerAggregation;
import mtsar.api.Task;

import java.util.Optional;

public interface AnswerAggregator {
    Optional<AnswerAggregation> aggregate(Task task);
}
