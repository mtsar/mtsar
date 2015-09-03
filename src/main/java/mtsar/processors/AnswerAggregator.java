/*
 * Copyright 2015 Dmitry Ustalov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
