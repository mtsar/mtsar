package mtsar.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TaskAllocation {
    private final Worker worker;
    private final Task task;
    private final Integer taskRemaining, taskCount;

    @JsonCreator
    public TaskAllocation(@JsonProperty("worker") Worker worker, @JsonProperty("task") Task task, @JsonProperty("taskRemaining") Integer remaining, @JsonProperty("taskCount") Integer tasks) {
        this.worker = worker;
        this.task = task;
        this.taskRemaining = remaining;
        this.taskCount = tasks;
    }

    @JsonProperty
    public String getType() {
        return getClass().getSimpleName();
    }

    @JsonProperty
    public Worker getWorker() {
        return worker;
    }

    @JsonProperty
    public Task getTask() {
        return task;
    }

    @JsonProperty
    public int getTaskRemaining() {
        return taskRemaining;
    }

    @JsonProperty
    public int getTaskCount() {
        return taskCount;
    }
}
