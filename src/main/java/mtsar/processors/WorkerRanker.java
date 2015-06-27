package mtsar.processors;

import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.api.WorkerRanking;

import java.util.Optional;

public interface WorkerRanker {
    Optional<WorkerRanking> rank(Worker worker);

    Optional<WorkerRanking> rank(Worker worker, Task task);
}
