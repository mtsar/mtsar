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

import com.ipeirotis.gal.algorithms.DawidSkene;
import com.ipeirotis.gal.core.AssignedLabel;
import com.ipeirotis.gal.core.Category;
import com.ipeirotis.gal.core.Datum;
import mtsar.api.*;
import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.WorkerRanker;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * A classical Dawid-Skene inference algorithm has been proposed far back in 1979.
 * This aggregator is driven by the well-known implementation by Sheng, Provost &amp; Ipeirotis.
 *
 * @see <a href="http://dl.acm.org/citation.cfm?id=1401965">10.1145/1401890.1401965</a>
 * @see <a href="http://www.jstor.org/stable/2346806">10.2307/2346806</a>
 */
public class DawidSkeneProcessor implements WorkerRanker, AnswerAggregator {
    public static <T> Comparator<T> comparingDouble(ToDoubleFunction<? super T> keyExtractor) {
        return Comparator.comparingDouble(keyExtractor).reversed();
    }

    private final Provider<Process> process;
    private final TaskDAO taskDAO;
    private final AnswerDAO answerDAO;

    @Inject
    public DawidSkeneProcessor(Provider<Process> process, TaskDAO taskDAO, AnswerDAO answerDAO) {
        this.process = requireNonNull(process);
        this.taskDAO = requireNonNull(taskDAO);
        this.answerDAO = requireNonNull(answerDAO);
    }

    @Override
    @Nonnull
    public Map<Integer, AnswerAggregation> aggregate(@Nonnull Collection<Task> tasks) {
        requireNonNull(process.get(), "the process provider should not provide null");
        if (tasks.isEmpty()) return Collections.emptyMap();
        final Map<Integer, Task> taskMap = getTaskMap();
        final DawidSkene ds = compute(taskMap);
        final Map<Integer, AnswerAggregation> results = ds.getObjects().values().stream().collect(Collectors.toMap(
                datum -> Integer.valueOf(datum.getName()),
                datum -> {
                    final Task task = taskMap.get(Integer.valueOf(datum.getName()));
                    final Map<String, Double> probabilities = datum.getProbabilityVector(Datum.ClassificationMethod.DS_Soft);
                    final Map.Entry<String, Double> winner = probabilities.entrySet().stream().sorted(comparingDouble(Map.Entry::getValue)).findFirst().get();
                    return new AnswerAggregation.Builder().setTask(task).addAnswers(winner.getKey()).build();
                }
        ));

        return results;
    }

    @Override
    @Nonnull
    public Map<Integer, WorkerRanking> rank(@Nonnull Collection<Worker> workers) {
        final Map<Integer, Task> taskMap = getTaskMap();
        final DawidSkene ds = compute(taskMap);
        ds.evaluateWorkers();
        final Map<Integer, WorkerRanking> rankings = workers.stream().collect(Collectors.toMap(Worker::getId,
                worker -> {
                    final com.ipeirotis.gal.core.Worker dsWorker = ds.getWorkers().get(worker.getId().toString());
                    final double reputation = dsWorker == null ? Double.NaN : dsWorker.getWorkerQuality(ds.getCategories(), com.ipeirotis.gal.core.Worker.ClassificationMethod.DS_MaxLikelihood_Estm);
                    return new WorkerRanking.Builder().setWorker(worker).setReputation(reputation).build();
                }
        ));
        return rankings;
    }

    protected Map<Integer, Task> getTaskMap() {
        return taskDAO.listForProcess(process.get().getId()).stream().collect(Collectors.toMap(Task::getId, Function.identity()));
    }

    protected DawidSkene compute(Map<Integer, Task> taskMap) {
        final Set<Category> categories = taskMap.values().stream().
                flatMap(task -> task.getAnswers().stream().map(answer -> new Category(answer))).
                collect(Collectors.toSet());

        final DawidSkene ds = new DawidSkene(categories);

        final List<Answer> answers = answerDAO.listForProcess(process.get().getId());

        for (final Answer answer : answers) {
            if (!answer.getType().equalsIgnoreCase(AnswerDAO.ANSWER_TYPE_ANSWER)) continue;
            for (final String label : answer.getAnswers()) {
                ds.addAssignedLabel(new AssignedLabel(
                        Integer.toString(answer.getWorkerId()),
                        Integer.toString(answer.getTaskId()),
                        label
                ));
            }
        }

        ds.estimate(taskMap.size() <= 50 ? 50 : taskMap.size(), 0.0001);

        return ds;
    }
}
