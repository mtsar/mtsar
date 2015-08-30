package mtsar.processors.answer;

import mtsar.api.AnswerAggregation;
import mtsar.api.Task;
import mtsar.processors.AnswerAggregator;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class EmptyAggregator implements AnswerAggregator {
    @Override
    public Map<Task, AnswerAggregation> aggregate(Collection<Task> tasks) {
        return Collections.emptyMap();
    }
}
