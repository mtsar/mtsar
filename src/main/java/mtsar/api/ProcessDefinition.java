package mtsar.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.Map;

@XmlRootElement
public class ProcessDefinition {
    protected String id;

    @Valid
    @NotNull
    protected String description, workerRankerName, taskAllocatorName, answerAggregatorName;

    protected Map<String, String> options = Collections.emptyMap();

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    @JsonProperty
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    public Map<String, String> getOptions() {
        return options;
    }

    @JsonProperty
    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    @JsonProperty("workerRanker")
    public String getWorkerRankerName() {
        return workerRankerName;
    }

    @JsonProperty("workerRanker")
    public void setWorkerRankerName(String workerRankerName) {
        this.workerRankerName = workerRankerName;
    }

    @JsonProperty("taskAllocator")
    public String getTaskAllocatorName() {
        return taskAllocatorName;
    }

    @JsonProperty("taskAllocator")
    public void setTaskAllocatorName(String taskAllocatorName) {
        this.taskAllocatorName = taskAllocatorName;
    }

    @JsonProperty("answerAggregator")
    public String getAnswerAggregatorName() {
        return answerAggregatorName;
    }

    @JsonProperty("answerAggregator")
    public void setAnswerAggregatorName(String answerAggregatorName) {
        this.answerAggregatorName = answerAggregatorName;
    }
}
