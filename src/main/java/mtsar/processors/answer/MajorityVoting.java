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

import mtsar.api.Answer;
import mtsar.api.AnswerAggregation;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.AnswerAggregator;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.mapping;

public class MajorityVoting implements AnswerAggregator {
    public final static Predicate<Task> SINGLE_TYPE = task -> task.getType().equalsIgnoreCase(TaskDAO.TASK_TYPE_SINGLE);
    public final static Predicate<Answer> VALID_ANSWER = answer -> answer.getType().equalsIgnoreCase(AnswerDAO.ANSWER_TYPE_ANSWER) && answer.getAnswer().isPresent();

    protected final Provider<Process> process;
    protected final AnswerDAO answerDAO;

    @Inject
    public MajorityVoting(Provider<Process> processProvider, AnswerDAO answerDAO) {
        this.process = processProvider;
        this.answerDAO = answerDAO;
    }

    @Override
    @Nonnull
    public Map<Integer, AnswerAggregation> aggregate(@Nonnull Collection<Task> tasks) {
        checkArgument(tasks.stream().allMatch(SINGLE_TYPE), "tasks should be of the type single");
        if (tasks.isEmpty()) return Collections.emptyMap();

        final List<Answer> answers = answerDAO.listForProcess(process.get().getId());
        if (answers.isEmpty()) return Collections.emptyMap();

        final Map<Integer, Task> taskMap = tasks.stream().collect(Collectors.toMap(Task::getId, Function.identity()));

        final Map<Integer, List<String>> answerMap = answers.stream().
                filter(answer -> taskMap.containsKey(answer.getTaskId())).filter(VALID_ANSWER).
                collect(Collectors.groupingBy(
                                Answer::getTaskId,
                                mapping(answer -> answer.getAnswer().get(), Collectors.toList()))
                );

        final Map<Integer, AnswerAggregation> result = taskMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> {
                    final List<String> answerList = answerMap.get(entry.getKey());
                    final String majorityVote = answerList.stream().reduce(
                            BinaryOperator.maxBy((o1, o2) -> Collections.frequency(answerList, o1) - Collections.frequency(answerList, o2))
                    ).orElse(null);
                    return new AnswerAggregation.Builder().
                            setTask(entry.getValue()).
                            addAnswers(majorityVote).
                            build();
                }
        ));

        return result;
    }
}
