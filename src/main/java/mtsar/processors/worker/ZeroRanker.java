package mtsar.processors.worker;

import mtsar.api.Worker;
import mtsar.api.WorkerRanking;
import mtsar.processors.WorkerRanker;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ZeroRanker implements WorkerRanker {
    @Override
    public Map<Worker, WorkerRanking> rank(Collection<Worker> workers) {
        return workers.stream().collect(Collectors.toMap(
                Function.identity(),
                worker -> new WorkerRanking.Builder().setWorker(worker).build()
        ));
    }
}
