package mtsar.processors.task;

import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.TaskAllocation;
import mtsar.api.Worker;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.TaskAllocator;

import javax.annotation.Nonnegative;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RandomAllocator implements TaskAllocator {
    protected final Provider<Process> process;
    protected final TaskDAO taskDAO;

    @Inject
    public RandomAllocator(Provider<Process> processProvider, TaskDAO taskDAO) {
        this.process = processProvider;
        this.taskDAO = taskDAO;
    }

    @Override
    public List<TaskAllocation> allocate(Worker worker, @Nonnegative int n) {
        final List<Task> tasks = taskDAO.listForProcess(process.get().getId());
        Collections.shuffle(tasks);
        return tasks.stream().limit(n).
                map(t -> new TaskAllocation(worker, t, tasks.size(), tasks.size())).
                collect(Collectors.toList());
    }
}
