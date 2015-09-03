package mtsar.processors;

import com.google.common.collect.Lists;
import mtsar.api.Worker;
import mtsar.api.WorkerRanking;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface WorkerRanker {
    /**
     * Given a collection of workers, estimate their performance.
     *
     * @param workers workers.
     * @return Worker rankings.
     */
    Map<Worker, WorkerRanking> rank(Collection<Worker> workers);

    /**
     * Given a worker, a ranker returns either a worker ranking, or nothing.
     * This is an alias for the method accepting the worker collection.
     *
     * @param worker worker.
     * @return Worker ranking.
     */
    default Optional<WorkerRanking> rank(Worker worker) {
        final Map<Worker, WorkerRanking> rankings = rank(Lists.newArrayList(worker));
        if (rankings.isEmpty()) return Optional.empty();
        return Optional.of(rankings.get(worker));
    }
}
