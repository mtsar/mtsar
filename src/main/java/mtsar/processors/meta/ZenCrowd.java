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

package mtsar.processors.meta;

import mtsar.api.*;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.WorkerRanker;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.square.qa.algorithms.ZenCrowdEM;
import org.square.qa.utilities.constructs.Models;
import org.square.qa.utilities.constructs.workersDataStruct;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * ZenCrowd algorithm for worker ranking and answer aggregation.
 *
 * @see <a href="http://dx.doi.org/10.1007/s00778-013-0324-z">10.1007/s00778-013-0324-z</a>
 * @see <a href="http://www.aaai.org/ocs/index.php/HCOMP/HCOMP13/paper/view/7550">HCOMP13/7550</a>
 */
public class ZenCrowd implements WorkerRanker, AnswerAggregator {
    private final Provider<Stage> stage;
    private final TaskDAO taskDAO;
    private final AnswerDAO answerDAO;

    @Inject
    public ZenCrowd(Provider<Stage> stage, TaskDAO taskDAO, AnswerDAO answerDAO) {
        this.stage = requireNonNull(stage);
        this.taskDAO = requireNonNull(taskDAO);
        this.answerDAO = requireNonNull(answerDAO);
    }

    @Nonnull
    @Override
    public Map<Integer, AnswerAggregation> aggregate(@Nonnull Collection<Task> tasks) {
        requireNonNull(stage.get(), "the stage provider should not provide null");
        if (tasks.isEmpty()) return Collections.emptyMap();
        final Map<Integer, Task> taskIds = tasks.stream().collect(Collectors.toMap(Task::getId, Function.identity()));
        final ZenCrowdEM<Integer, Integer, String> zenCrowd = compute(getTaskMap());
        final Map<Integer, AnswerAggregation> aggregations = zenCrowd.getCurrentModel().getCombinedEstLabels().entrySet().stream().
                filter(entry -> taskIds.containsKey(entry.getKey())).
                collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> new AnswerAggregation.Builder().setTask(taskIds.get(entry.getKey())).addAnswers(entry.getValue().getFirst()).build()
                ));
        return aggregations;
    }

    @Nonnull
    @Override
    public Map<Integer, WorkerRanking> rank(@Nonnull Collection<Worker> workers) {
        requireNonNull(stage.get(), "the stage provider should not provide null");
        if (workers.isEmpty()) return Collections.emptyMap();
        final Map<Integer, Worker> workerIds = workers.stream().collect(Collectors.toMap(Worker::getId, Function.identity()));
        final ZenCrowdEM<Integer, Integer, String> zenCrowd = compute(getTaskMap());
        try {
            @SuppressWarnings("unchecked") final Map<Integer, Double> reliability = (Map<Integer, Double>) FieldUtils.readField(zenCrowd, "workerReliabilityMap", true);
            final Map<Integer, WorkerRanking> rankings = reliability.entrySet().stream().
                    filter(entry -> workerIds.containsKey(entry.getKey())).
                    collect(Collectors.toMap(Map.Entry::getKey,
                            entry -> new WorkerRanking.Builder().setWorker(workerIds.get(entry.getKey())).setReputation(entry.getValue()).build()
                    ));
            return rankings;
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected Map<Integer, Task> getTaskMap() {
        return taskDAO.listForStage(stage.get().getId()).stream().collect(Collectors.toMap(Task::getId, Function.identity()));
    }

    protected ZenCrowdEM<Integer, Integer, String> compute(Map<Integer, Task> taskMap) {
        final Models<Integer, Integer, String> models = new Models<>();

        final Set<String> categories = taskMap.values().stream().flatMap(t -> t.getAnswers().stream()).collect(Collectors.toSet());
        models.setResponseCategories(new TreeSet<>(categories));

        final Map<Integer, workersDataStruct<Integer, String>> workers = new HashMap<>();
        final List<Answer> answers = answerDAO.listForStage(stage.get().getId());
        for (final Answer answer : answers) {
            if (!answer.getType().equalsIgnoreCase(AnswerDAO.ANSWER_TYPE_ANSWER)) continue;
            if (answer.getAnswers().isEmpty()) continue;
            if (!workers.containsKey(answer.getWorkerId()))
                workers.put(answer.getWorkerId(), new workersDataStruct<>());
            final workersDataStruct<Integer, String> datum = workers.get(answer.getWorkerId());
            datum.insertWorkerResponse(answer.getTaskId(), answer.getAnswer().get());
        }
        models.setWorkersMap(workers);

        final ZenCrowdEM<Integer, Integer, String> zenCrowd = new ZenCrowdEM<>(models.getZenModel());
        zenCrowd.computeLabelEstimates();

        return zenCrowd;
    }
}
