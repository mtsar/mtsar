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

package mtsar.processors.answer;

import mtsar.api.AnswerAggregation;
import mtsar.api.Stage;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.SQUARE;
import org.square.qa.algorithms.MajorityVoteGeneralized;
import org.square.qa.utilities.constructs.Models;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public class MajorityVoting extends SQUARE implements AnswerAggregator {
    protected final static Predicate<Task> SINGLE_TYPE = task -> task.getType().equalsIgnoreCase(TaskDAO.TASK_TYPE_SINGLE);
    @Inject
    protected Stage stage;
    protected final AnswerDAO answerDAO;

    @Inject
    protected MajorityVoting(AnswerDAO answerDAO) {
        this.answerDAO = requireNonNull(answerDAO);
    }

    public MajorityVoting(Stage stage, AnswerDAO answerDAO) {
        this(answerDAO);
        this.stage = requireNonNull(stage);
    }

    @Nonnull
    @Override
    public Map<Integer, AnswerAggregation> aggregate(@Nonnull Collection<Task> tasks) {
        requireNonNull(stage, "the stage provider should not provide null");
        checkArgument(tasks.stream().allMatch(SINGLE_TYPE), "tasks should be of the type single");
        if (tasks.isEmpty()) return Collections.emptyMap();
        final Map<Integer, Task> taskIds = tasks.stream().collect(Collectors.toMap(Task::getId, Function.identity()));
        final Models.MajorityModel<Integer, Integer, String> majorityModel = compute(stage, answerDAO, taskIds).getMajorityModel();
        final MajorityVoteGeneralized<Integer, Integer, String> majorityVoting = new MajorityVoteGeneralized<>(majorityModel);
        majorityVoting.computeLabelEstimates();
        final Map<Integer, AnswerAggregation> aggregations = majorityVoting.getCurrentModel().getCombinedEstLabels().entrySet().stream().
                filter(entry -> taskIds.containsKey(entry.getKey())).
                collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> new AnswerAggregation.Builder().setTask(taskIds.get(entry.getKey())).addAnswers(entry.getValue().getFirst()).build()
                ));
        return aggregations;
    }
}
