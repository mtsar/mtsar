package mtsar.processors;

import mtsar.api.Task;
import mtsar.api.Worker;

public interface WorkerRanker {
    double rank(Worker worker);

    double rank(Worker worker, Task task);
}
