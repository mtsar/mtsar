package mtsar.processors.worker;

import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.api.WorkerRanking;
import mtsar.processors.WorkerRanker;

import java.util.Optional;

public class ZeroRanker implements WorkerRanker {
    @Override
    public Optional<WorkerRanking> rank(Worker worker) {
        return Optional.of(new WorkerRanking.Builder().setWorker(worker).build());
    }

    @Override
    public Optional<WorkerRanking> rank(Worker worker, Task task) {
        return Optional.of(new WorkerRanking.Builder().setWorker(worker).build());
    }
}
