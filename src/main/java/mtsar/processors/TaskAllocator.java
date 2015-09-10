/*
 * Copyright 2015 Dmitry Ustalov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mtsar.processors;

import mtsar.api.TaskAllocation;
import mtsar.api.Worker;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Task allocator is a processor that allocates tasks to the crowd workers.
 */
public interface TaskAllocator {
    /**
     * Given a worker, an allocator returns either an allocated task, or nothing.
     *
     * @param worker worker.
     * @param n      maximum number of tasks to be allocated.
     * @return Allocated tasks.
     */
    @Nonnull
    Optional<TaskAllocation> allocate(@Nonnull Worker worker, @Nonnegative int n);

    /**
     * Given a worker, an allocator returns either an allocated task, or nothing.
     * This is an alias for the method accepting the number of allocated tasks.
     *
     * @param worker worker.
     * @return Allocated task.
     */
    @Nonnull
    default Optional<TaskAllocation> allocate(@Nonnull Worker worker) {
        return allocate(worker, 1);
    }
}
