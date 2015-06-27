package mtsar.processors.worker;

import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.api.WorkerRanking;
import mtsar.processors.WorkerRanker;

import java.util.Optional;

public class RandomRanker implements WorkerRanker {
    @Override
    public Optional<WorkerRanking> rank(Worker worker) {
        return Optional.of(WorkerRanking.create(worker, Math.random()));
    }

    @Override
    public Optional<WorkerRanking> rank(Worker worker, Task task) {
        return Optional.of(WorkerRanking.create(worker, Math.random()));
    }
}
