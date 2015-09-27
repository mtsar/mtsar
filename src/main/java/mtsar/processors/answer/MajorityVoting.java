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
import org.square.qa.algorithms.MajorityVoteGeneralized;
import org.square.qa.utilities.constructs.Models;
import org.square.qa.utilities.constructs.workersDataStruct;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public class MajorityVoting implements AnswerAggregator {
    public final static Predicate<Task> SINGLE_TYPE = task -> task.getType().equalsIgnoreCase(TaskDAO.TASK_TYPE_SINGLE);

    protected final Provider<Process> process;
    protected final AnswerDAO answerDAO;

    @Inject
    public MajorityVoting(Provider<Process> processProvider, AnswerDAO answerDAO) {
        this.process = requireNonNull(processProvider);
        this.answerDAO = requireNonNull(answerDAO);
    }

    @Nonnull
    @Override
    public Map<Integer, AnswerAggregation> aggregate(@Nonnull Collection<Task> tasks) {
        requireNonNull(process.get(), "the process provider should not provide null");
        checkArgument(tasks.stream().allMatch(SINGLE_TYPE), "tasks should be of the type single");
        if (tasks.isEmpty()) return Collections.emptyMap();
        final Map<Integer, Task> taskIds = tasks.stream().collect(Collectors.toMap(Task::getId, Function.identity()));
        final MajorityVoteGeneralized<Integer, Integer, String> majorityVoting = compute(taskIds);
        final Map<Integer, AnswerAggregation> aggregations = majorityVoting.getCurrentModel().getCombinedEstLabels().entrySet().stream().
                filter(entry -> taskIds.containsKey(entry.getKey())).
                collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> new AnswerAggregation.Builder().setTask(taskIds.get(entry.getKey())).addAnswers(entry.getValue().getFirst()).build()
                ));
        return aggregations;
    }

    protected MajorityVoteGeneralized<Integer, Integer, String> compute(Map<Integer, Task> taskMap) {
        final Models<Integer, Integer, String> models = new Models<>();

        final Set<String> categories = taskMap.values().stream().flatMap(t -> t.getAnswers().stream()).collect(Collectors.toSet());
        models.setResponseCategories(new TreeSet<>(categories));

        final Map<Integer, workersDataStruct<Integer, String>> workers = new HashMap<>();
        final List<Answer> answers = answerDAO.listForProcess(process.get().getId());
        for (final Answer answer : answers) {
            if (!answer.getType().equalsIgnoreCase(AnswerDAO.ANSWER_TYPE_ANSWER)) continue;
            if (answer.getAnswers().isEmpty()) continue;
            if (!workers.containsKey(answer.getWorkerId()))
                workers.put(answer.getWorkerId(), new workersDataStruct<>());
            final workersDataStruct<Integer, String> datum = workers.get(answer.getWorkerId());
            datum.insertWorkerResponse(answer.getTaskId(), answer.getAnswer().get());
        }
        models.setWorkersMap(workers);

        final MajorityVoteGeneralized<Integer, Integer, String> majorityVoting = new MajorityVoteGeneralized<>(models.getMajorityModel());
        majorityVoting.computeLabelEstimates();
        return majorityVoting;
    }
}
