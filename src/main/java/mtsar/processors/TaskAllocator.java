package mtsar.processors;

import mtsar.api.TaskAllocation;
import mtsar.api.Worker;

import javax.annotation.Nonnegative;
import java.util.List;
import java.util.Optional;

/**
 * Task allocator is a processor that allocates tasks to the crowd workers.
 */
public interface TaskAllocator {
    /**
     * Given a worker, an allocator returns either an allocated task, or nothing.
     *
     * @param w worker.
     * @param n maximum number of tasks to be allocated.
     * @return Allocated tasks.
     */
    List<TaskAllocation> allocate(Worker w, @Nonnegative int n);

    /**
     * Given a worker, an allocator returns either an allocated task, or nothing.
     * This is an alias for the method accepting the number of allocated tasks.
     *
     * @param w worker.
     * @return Allocated task.
     */
    default Optional<TaskAllocation> allocate(Worker w) {
        final List<TaskAllocation> allocations = allocate(w, 1);
        if (allocations.isEmpty()) return Optional.empty();
        return Optional.ofNullable(allocations.get(0));
    }
}
