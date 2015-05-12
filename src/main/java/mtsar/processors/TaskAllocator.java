package mtsar.processors;

import mtsar.api.Task;
import mtsar.api.Worker;

import java.util.Optional;

public interface TaskAllocator {
    Optional<Task> allocate(Worker w);
}
