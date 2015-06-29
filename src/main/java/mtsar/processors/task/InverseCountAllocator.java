package mtsar.processors.task;

import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.TaskAllocation;
import mtsar.api.Worker;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.TaskAllocator;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InverseCountAllocator implements TaskAllocator {
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
        if (tasks.isEmpty()) return Optional.empty();

        final Map<Task, Integer> counts = tasks.parallelStream().collect(
                Collectors.toMap(Function.identity(), t -> answerDAO.listForTask(t.getId(), process.get().getId()).size())
        );

        tasks.sort((t1, t2) -> counts.get(t1).compareTo(counts.get(t2)));

        return Optional.of(new TaskAllocation(worker, tasks.get(0)));
    }
}
