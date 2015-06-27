package mtsar.processors;

import mtsar.api.TaskAllocation;
import mtsar.api.Worker;

import java.util.Optional;

public interface TaskAllocator {
    Optional<TaskAllocation> allocate(Worker w);
}
