package mtsar.processors;

import com.google.common.collect.Lists;
import mtsar.api.AnswerAggregation;
import mtsar.api.Task;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface AnswerAggregator {
    /**
     * Given a collection of tasks, an aggregator maps these tasks to the aggregated answers.
     *
     * @param tasks tasks.
     * @return Aggregated answers.
     */
    Map<Task, AnswerAggregation> aggregate(Collection<Task> tasks);

    /**
     * Given a task, an aggregator returns either an aggregated answer, or nothing.
     * This is an alias for the method accepting the task collection.
     *
     * @param task task.
     * @return Aggregated answer.
     */
    default Optional<AnswerAggregation> aggregate(Task task) {
        final Map<Task, AnswerAggregation> aggregations = aggregate(Lists.newArrayList(task));
        if (aggregations.isEmpty()) return Optional.empty();
        return Optional.of(aggregations.get(task));
    }
}
