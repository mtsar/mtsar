package mtsar.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface TaskAllocation {
    static TaskAllocation create(Worker worker, Task task) {
        return new TaskAllocation() {
            @Override
            public Worker getWorker() {
                return worker;
            }

            @Override
            public Task getTask() {
                return task;
            }
        };
    }

    @JsonProperty
    Worker getWorker();

    @JsonProperty
    Task getTask();
}
