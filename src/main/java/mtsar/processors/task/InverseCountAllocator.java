package mtsar.processors.task;

import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.TaskAllocation;
import mtsar.api.Worker;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.TaskAllocator;
import org.apache.commons.collections4.comparators.ComparatorChain;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InverseCountAllocator implements TaskAllocator {
    class AnswerCountComparator implements Comparator<Task> {
        private final Map<Task, Integer> counts;

        public AnswerCountComparator(Map<Task, Integer> counts) {
            this.counts = counts;
        }

        @Override
        public int compare(Task t1, Task t2) {
            return counts.get(t1).compareTo(counts.get(t2));
        }
    }

    protected final Provider<Process> process;
    protected final TaskDAO taskDAO;
    protected final AnswerDAO answerDAO;

    @Inject
    public InverseCountAllocator(Provider<Process> processProvider, TaskDAO taskDAO, AnswerDAO answerDAO) {
        this.process = processProvider;
        this.taskDAO = taskDAO;
        this.answerDAO = answerDAO;
    }

    @Override
    public Optional<TaskAllocation> allocate(Worker worker) {
        List<Task> tasks = taskDAO.listForProcess(process.get().getId());
        final Map<Task, Integer> counts = getCounts(tasks, worker);

        final Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            final Task task = iterator.next();
            if (!counts.containsKey(task)) iterator.remove();
        }

        if (tasks.isEmpty()) return Optional.empty();
        tasks.sort(getComparator(counts, worker));

        return Optional.of(new TaskAllocation(worker, tasks.get(0)));
    }

    protected Map<Task, Integer> getCounts(List<Task> tasks, Worker worker) {
        return tasks.parallelStream().collect(
                Collectors.toMap(Function.identity(), t -> answerDAO.listForTask(t.getId(), process.get().getId()).size())
        );
    }

    private Comparator<Task> getComparator(Map<Task, Integer> counts, Worker worker) {
        final ComparatorChain<Task> comparator = new ComparatorChain<>();
        comparator.addComparator(new AnswerCountComparator(counts));
        comparator.addComparator(Comparator.comparing(o -> o.hashCode() % worker.getId()));
        return comparator;
    }
}
