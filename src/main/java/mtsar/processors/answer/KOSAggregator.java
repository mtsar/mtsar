package mtsar.processors.answer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;
import mtsar.api.Answer;
import mtsar.api.AnswerAggregation;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.AnswerAggregator;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Implementation of the answer aggregation algorithm proposed by Karger, Oh & Shah for binary tasks.
 * <p>
 * This code is not verified, thus it provides random results and breaks unit tests.
 * Do not use it now, please.
 *
 * @see <a href="http://pubsonline.informs.org/doi/abs/10.1287/opre.2013.1235">10.1287/opre.2013.1235</a>
 * @see MajorityVoting
 */
public class KOSAggregator implements AnswerAggregator {
    public final static Predicate<Task> SINGLE_BINARY_TYPE = task -> task.getAnswers().size() == 2 && task.getType().equalsIgnoreCase(TaskDAO.TASK_TYPE_SINGLE);

    private final Provider<Process> process;
    private final TaskDAO taskDAO;
    private final AnswerDAO answerDAO;

    @Inject
    public KOSAggregator(Provider<Process> process, TaskDAO taskDAO, AnswerDAO answerDAO) {
        this.process = process;
        this.taskDAO = taskDAO;
        this.answerDAO = answerDAO;
    }

    @Override
    public Map<Task, AnswerAggregation> aggregate(Collection<Task> tasks) {
        checkArgument(tasks.stream().allMatch(SINGLE_BINARY_TYPE), "tasks should be of the type single and have only two possible answers");
        if (tasks.isEmpty()) return Collections.emptyMap();

        final List<Answer> answers = answerDAO.listForProcess(process.get().getId());
        if (answers.isEmpty()) return Collections.emptyMap();

        final Map<Integer, Task> taskMap = taskDAO.listForProcess(process.get().getId()).stream().
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
        return estimations.entrySet().stream().collect(Collectors.toMap(
                estimation -> taskMap.get(estimation.getKey()),
                estimation -> {
                    final String answer = answerIndex.get(estimation.getKey()).inverse().get(estimation.getValue() < 0 ? (short) -1 : (short) +1);
                    return new AnswerAggregation.Builder().setTask(taskMap.get(estimation.getKey())).addAnswers(answer).build();
                }
        ));
    }

    protected Map<Integer, Double> converge(Table<Integer, Integer, Short> graph, int kMax) {
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

    protected Table<Integer, Integer, Double> tasksUpdate(Table<Integer, Integer, Short> graph, Table<Integer, Integer, Double> ys) {
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

    protected Table<Integer, Integer, Double> workersUpdate(Table<Integer, Integer, Short> graph, Table<Integer, Integer, Double> xs) {
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
