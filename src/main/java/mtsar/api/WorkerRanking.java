package mtsar.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface WorkerRanking {
    static WorkerRanking create(Worker worker, double reputation) {
        return new WorkerRanking() {
            @Override
            public Worker getWorker() {
                return worker;
            }

            @Override
            public double getReputation() {
                return reputation;
            }
        };
    }

    @JsonProperty
    Worker getWorker();

    @JsonProperty
    double getReputation();
}
