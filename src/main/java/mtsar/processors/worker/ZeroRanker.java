package mtsar.processors.worker;

import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.processors.WorkerRanker;

public class ZeroRanker implements WorkerRanker {
    @Override
    public double rank(Worker worker) {
        return 0;
    }

    @Override
    public double rank(Worker worker, Task task) {
        return 0;
    }
}
