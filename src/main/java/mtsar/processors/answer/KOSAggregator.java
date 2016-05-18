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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;
import mtsar.api.Answer;
import mtsar.api.AnswerAggregation;
import mtsar.api.Stage;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.AnswerAggregator;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Implementation of the answer aggregation algorithm proposed by Karger, Oh &amp; Shah for binary tasks.
 * <p>
 * This code is not verified, thus it provides random results and breaks unit tests.
 * Do not use it now, please.
 *
 * @see <a href="http://pubsonline.informs.org/doi/abs/10.1287/opre.2013.1235">10.1287/opre.2013.1235</a>
 * @see MajorityVoting
 */
public class KOSAggregator implements AnswerAggregator {
    protected final static Predicate<Task> SINGLE_BINARY_TYPE = task -> task.getAnswers().size() == 2 && task.getType().equalsIgnoreCase(TaskDAO.TASK_TYPE_SINGLE);
    @Inject
    protected Stage stage;
    protected final TaskDAO taskDAO;
    protected final AnswerDAO answerDAO;

    KOSAggregator(Stage stage, TaskDAO taskDAO, AnswerDAO answerDAO) {
        this(taskDAO, answerDAO);
        this.stage = stage;
    }
    @Inject
    public KOSAggregator(TaskDAO taskDAO, AnswerDAO answerDAO) {
        this.taskDAO = requireNonNull(taskDAO);
        this.answerDAO = requireNonNull(answerDAO);
    }

    @Override
    @Nonnull
    public Map<Integer, AnswerAggregation> aggregate(@Nonnull Collection<Task> tasks) {
        requireNonNull(stage, "the stage provider should not provide null");
        checkArgument(tasks.stream().allMatch(SINGLE_BINARY_TYPE), "tasks should be of the type single and have only two possible answers");
        if (tasks.isEmpty()) return Collections.emptyMap();

        final List<Answer> answers = answerDAO.listForStage(stage.getId());
        if (answers.isEmpty()) return Collections.emptyMap();

        final Map<Integer, Task> taskMap = taskDAO.listForStage(stage.getId()).stream().
                filter(SINGLE_BINARY_TYPE).collect(Collectors.toMap(Task::getId, Function.identity()));

        final Map<Integer, BiMap<String, Short>> answerIndex = taskMap.values().stream().collect(Collectors.toMap(Task::getId,
                task -> {
                    final BiMap<String, Short> map = HashBiMap.create(2);
                    map.put(task.getAnswers().get(0), (short) -1);
                    map.put(task.getAnswers().get(1), (short) +1);
                    return map;
                }
        ));

        /* rows are tasks IDs, columns are answer IDs, values are answers */
        final Table<Integer, Integer, Short> graph = HashBasedTable.create();

        for (final Answer answer : answers) {
            if (!answer.getType().equalsIgnoreCase(AnswerDAO.ANSWER_TYPE_ANSWER)) continue;
            graph.put(answer.getTaskId(), answer.getWorkerId(), answerIndex.get(answer.getTaskId()).get(answer.getAnswers().get(0)));
        }

        final Map<Integer, Double> estimations = converge(graph, 10);
        return estimations.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                estimation -> {
                    final String answer = answerIndex.get(estimation.getKey()).inverse().get(estimation.getValue() < 0 ? (short) -1 : (short) +1);
                    return new AnswerAggregation.Builder().setTask(taskMap.get(estimation.getKey())).addAnswers(answer).build();
                }
        ));
    }

    private Map<Integer, Double> converge(Table<Integer, Integer, Short> graph, int kMax) {
        final RealDistribution distribution = new NormalDistribution(1, 1);

        Table<Integer, Integer, Double> ys = HashBasedTable.create(graph.rowKeySet().size(), graph.columnKeySet().size());

        for (final Table.Cell<Integer, Integer, Short> cell : graph.cellSet()) {
            ys.put(cell.getRowKey(), cell.getColumnKey(), distribution.sample());
        }

        for (int k = 1; k <= kMax; k++) {
            final Table<Integer, Integer, Double> xs = tasksUpdate(graph, ys);
            if (k < kMax) ys = workersUpdate(graph, xs);
        }

        final Map<Integer, Double> estimations = new HashMap<>();

        for (final Integer taskId : graph.rowKeySet()) {
            double sumProduct = 0.0;

            final Map<Integer, Double> workers = ys.row(taskId);
            for (final Map.Entry<Integer, Double> worker : workers.entrySet()) {
                sumProduct += graph.get(taskId, worker.getKey()) * worker.getValue();
            }

            estimations.put(taskId, sumProduct);
        }

        return estimations;
    }

    private Table<Integer, Integer, Double> tasksUpdate(Table<Integer, Integer, Short> graph, Table<Integer, Integer, Double> ys) {
        final Table<Integer, Integer, Double> xs = HashBasedTable.create(graph.rowKeySet().size(), graph.columnKeySet().size());

        for (final Table.Cell<Integer, Integer, Short> cell : graph.cellSet()) {
            double sumProduct = 0.0;

            final int taskId = cell.getRowKey(), workerId = cell.getColumnKey();
            final Map<Integer, Short> workers = graph.row(taskId);

            for (final Map.Entry<Integer, Short> worker : workers.entrySet()) {
                if (worker.getKey() == workerId) continue;
                sumProduct += worker.getValue() * ys.get(taskId, worker.getKey());
            }

            xs.put(taskId, workerId, sumProduct);
        }

        return xs;
    }

    private Table<Integer, Integer, Double> workersUpdate(Table<Integer, Integer, Short> graph, Table<Integer, Integer, Double> xs) {
        final Table<Integer, Integer, Double> ys = HashBasedTable.create(graph.rowKeySet().size(), graph.columnKeySet().size());

        for (final Table.Cell<Integer, Integer, Short> cell : graph.cellSet()) {
            double sumProduct = 0.0;

            final int taskId = cell.getRowKey(), workerId = cell.getColumnKey();
            final Map<Integer, Short> tasks = graph.column(workerId);

            for (final Map.Entry<Integer, Short> task : tasks.entrySet()) {
                if (task.getKey() == taskId) continue;
                sumProduct += task.getValue() * xs.get(task.getKey(), workerId);
            }

            ys.put(taskId, workerId, sumProduct);
        }

        return ys;
    }
}
