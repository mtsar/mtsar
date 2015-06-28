package mtsar.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TaskAllocation {
    private final Worker worker;
    private final Task task;

    @JsonCreator
    public TaskAllocation(@JsonProperty("worker") Worker worker, @JsonProperty("task") Task task) {
        this.worker = worker;
        this.task = task;
    }

    @JsonProperty
    public Worker getWorker() {
        return worker;
    }

    @JsonProperty
    public Task getTask() {
        return task;
    }
}
