package mtsar.processors;

import mtsar.api.Task;
import mtsar.api.Worker;

public interface WorkerRanker {
    double estimatePerformance(Worker worker);
    double estimatePerformance(Worker worker, Task task);
}
