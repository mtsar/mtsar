package mtsar.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement
public class ProcessDefinition {
    public static class Builder {
        protected String id, description, workerRanker, taskAllocator, answerAggregator;
        protected Map<String, String> options;
        protected Timestamp dateTime;

        public ProcessDefinition build() {
            return new ProcessDefinition(this);
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setOptions(Map<String, String> options) {
            this.options = options;
            return this;
        }

        public Builder setOptions(String optionsJSON) {
            try {
                this.options = new HashMap<>();
                this.options.putAll(new ObjectMapper().readValue(optionsJSON, new TypeReference<Map<String, String>>() {
                }));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public Builder setWorkerRanker(String workerRanker) {
            this.workerRanker = workerRanker;
            return this;
        }

        public Builder setTaskAllocator(String taskAllocator) {
            this.taskAllocator = taskAllocator;
            return this;
        }

        public Builder setAnswerAggregator(String answerAggregator) {
            this.answerAggregator = answerAggregator;
            return this;
        }

        public Builder setDateTime(Timestamp dateTime) {
            this.dateTime = dateTime;
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    protected ProcessDefinition(Builder builder) {
        this.id = builder.id;
        this.description = builder.description;
        this.workerRanker = builder.workerRanker;
        this.taskAllocator = builder.taskAllocator;
        this.answerAggregator = builder.answerAggregator;
        this.options = builder.options;
    }

    protected String id, description, workerRanker, taskAllocator, answerAggregator;
    protected Timestamp dateTime;

    protected Map<String, String> options = Collections.emptyMap();

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    @JsonProperty
    public Map<String, String> getOptions() {
        return options;
    }

    @JsonIgnore
    public String getOptionsJSON() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(options == null ? Collections.emptyMap() : options);
    }

    @JsonProperty()
    public String getWorkerRanker() {
        return workerRanker;
    }

    @JsonProperty()
    public String getTaskAllocator() {
        return taskAllocator;
    }

    @JsonProperty()
    public String getAnswerAggregator() {
        return answerAggregator;
    }

    @JsonProperty
    public Timestamp getDateTime() {
        return dateTime;
    }
}
