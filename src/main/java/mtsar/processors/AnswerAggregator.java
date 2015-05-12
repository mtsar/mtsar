package mtsar.processors;

import mtsar.api.Answer;
import mtsar.api.Task;

import java.util.Optional;

public interface AnswerAggregator {
    Optional<Answer> aggregate(Task task);
}
