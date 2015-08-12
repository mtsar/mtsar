package mtsar.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WorkerRanking {
    private final Worker worker;
    private final double reputation;

    @JsonCreator
    public WorkerRanking(@JsonProperty("worker") Worker worker, @JsonProperty("reputation") double reputation) {
        this.worker = worker;
        this.reputation = reputation;
    }

    @JsonProperty
    public String getType() { return getClass().getSimpleName(); }

    @JsonProperty
    public Worker getWorker() {
        return worker;
    }

    @JsonProperty
    public double getReputation() {
        return reputation;
    }
}
