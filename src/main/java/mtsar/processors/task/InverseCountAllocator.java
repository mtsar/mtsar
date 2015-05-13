package mtsar.processors.task;

import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.api.jdbi.AnswerDAO;
import mtsar.api.jdbi.TaskDAO;
import mtsar.processors.Processor;
import mtsar.processors.TaskAllocator;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InverseCountAllocator extends Processor implements TaskAllocator {
    public static final Comparator<Task> RANDOM_COMPARATOR = Comparator.comparing(t -> Math.random());

    private final TaskDAO taskDAO;
    private final AnswerDAO answerDAO;

    @Inject
    public InverseCountAllocator(TaskDAO taskDAO, AnswerDAO answerDAO) {
        this.taskDAO = taskDAO;
        this.answerDAO = answerDAO;
    }

    @Override
    public Optional<Task> allocate(Worker w) {
        final Comparator<Task> comparator = getComparator(answerDAO).thenComparing(RANDOM_COMPARATOR);
        final List<Task> tasks = taskDAO.listForProcess(process.getId());
        return tasks.stream().sorted(comparator).findFirst();
    }

    private Comparator<Task> getComparator(AnswerDAO answerDAO) {
        return Comparator.comparing(task -> answerDAO.listForTask(task.getId(), process.getId()).size());
    }
}
