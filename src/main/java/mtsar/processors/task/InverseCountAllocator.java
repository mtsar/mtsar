package mtsar.processors.task;

import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.TaskAllocation;
import mtsar.api.Worker;
import mtsar.api.jdbi.AnswerDAO;
import mtsar.api.jdbi.TaskDAO;
import mtsar.processors.TaskAllocator;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class InverseCountAllocator implements TaskAllocator {
    public static final Comparator<Task> RANDOM_COMPARATOR = Comparator.comparing(t -> Math.random());

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
        final Comparator<Task> comparator = getComparator(answerDAO).thenComparing(RANDOM_COMPARATOR);
        final List<Task> tasks = taskDAO.listForProcess(process.get().getId());
        final Optional<Task> task = tasks.stream().sorted(comparator).findFirst();
        if (!task.isPresent()) return Optional.empty();
        return Optional.of(new TaskAllocation(worker, task.get()));
    }

    private Comparator<Task> getComparator(AnswerDAO answerDAO) {
        return Comparator.comparing(task -> answerDAO.listForTask(task.getId(), process.get().getId()).size());
    }
}
