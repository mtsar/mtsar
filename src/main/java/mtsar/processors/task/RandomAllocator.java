package mtsar.processors.task;

import mtsar.api.Task;
import mtsar.api.jdbi.TaskDAO;
import mtsar.api.Worker;
import mtsar.processors.Processor;
import mtsar.processors.TaskAllocator;

import javax.inject.Inject;
import java.util.Optional;

public class RandomAllocator extends Processor implements TaskAllocator {
    private final TaskDAO taskDAO;

    @Inject
    public RandomAllocator(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    @Override
    public Optional<Task> allocate(Worker w) {
        return Optional.ofNullable(taskDAO.random(getProcess().getId()));
    }
}
