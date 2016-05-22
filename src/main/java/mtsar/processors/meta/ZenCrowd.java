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
import mtsar.processors.SQUARE;
import mtsar.processors.WorkerRanker;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.square.qa.algorithms.ZenCrowdEM;
import org.square.qa.utilities.constructs.Models;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * ZenCrowd algorithm for worker ranking and answer aggregation.
 *
 * @see <a href="http://dx.doi.org/10.1007/s00778-013-0324-z">10.1007/s00778-013-0324-z</a>
 */
public class ZenCrowd extends SQUARE implements WorkerRanker, AnswerAggregator {
    @Inject
    private Stage stage;
    private final TaskDAO taskDAO;
    private final AnswerDAO answerDAO;

    ZenCrowd(Stage stage, TaskDAO taskDAO, AnswerDAO answerDAO) {
        this(taskDAO, answerDAO);
        this.stage = stage;
    }

    @Inject
    public ZenCrowd(TaskDAO taskDAO, AnswerDAO answerDAO) {
        this.taskDAO = requireNonNull(taskDAO);
        this.answerDAO = requireNonNull(answerDAO);
    }

    @Nonnull
    @Override
    public Map<Integer, AnswerAggregation> aggregate(@Nonnull Collection<Task> tasks) {
        requireNonNull(stage, "the stage provider should not provide null");
        if (tasks.isEmpty()) return Collections.emptyMap();
        final Map<Integer, Task> taskIds = tasks.stream().collect(Collectors.toMap(Task::getId, Function.identity()));
        final Models.ZenModel<Integer, Integer, String> zenModel = compute(stage, answerDAO, getTaskMap()).getZenModel();
        final ZenCrowdEM<Integer, Integer, String> zenCrowd = new ZenCrowdEM<>(zenModel);
        zenCrowd.computeLabelEstimates();
        final Map<Integer, AnswerAggregation> aggregations = zenCrowd.getCurrentModel().getCombinedEstLabels().entrySet().stream().
                filter(entry -> taskIds.containsKey(entry.getKey())).
                collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> new AnswerAggregation.Builder().
                                setTask(taskIds.get(entry.getKey())).
                                addAnswers(entry.getValue().getFirst()).
                                addConfidences(entry.getValue().getSecond().get(entry.getValue().getFirst())).
                                build()
                ));
        return aggregations;
    }

    @Nonnull
    @Override
    public Map<Integer, WorkerRanking> rank(@Nonnull Collection<Worker> workers) {
        requireNonNull(stage, "the stage provider should not provide null");
        if (workers.isEmpty()) return Collections.emptyMap();
        final Map<Integer, Worker> workerIds = workers.stream().collect(Collectors.toMap(Worker::getId, Function.identity()));
        final Models.ZenModel<Integer, Integer, String> zenModel = compute(stage, answerDAO, getTaskMap()).getZenModel();
        final ZenCrowdEM<Integer, Integer, String> zenCrowd = new ZenCrowdEM<>(zenModel);
        zenCrowd.computeLabelEstimates();
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

    private Map<Integer, Task> getTaskMap() {
        return taskDAO.listForStage(stage.getId()).stream().collect(Collectors.toMap(Task::getId, Function.identity()));
    }
}
