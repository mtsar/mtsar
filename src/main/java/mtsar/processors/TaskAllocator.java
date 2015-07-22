package mtsar.processors;

import mtsar.api.TaskAllocation;
import mtsar.api.Worker;

import java.util.Optional;

/**
 * Task allocator is a processor that allocates tasks to the crowd workers.
 */
public interface TaskAllocator {
    /**
     * Given a worker, an allocator returns either an allocated task, or nothing.
     * @param w worker.
     * @return Allocated task.
     */
    Optional<TaskAllocation> allocate(Worker w);
}
