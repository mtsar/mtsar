package mtsar.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.TaskAllocator;
import mtsar.processors.WorkerRanker;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
@XmlRootElement
public class Process {
    public static Provider<Process> wrap(Process process) {
        return () -> process;
    }

    protected String id;
    protected Map<String, String> options;

    protected final WorkerRanker workerRanker;
    protected final TaskAllocator taskAllocator;
    protected final AnswerAggregator answerAggregator;

    @Inject
    public Process(@Named("id") String id, @Named("options") Map<String, String> options, WorkerRanker workerRanker, TaskAllocator taskAllocator, AnswerAggregator answerAggregator, Logger logger) {
        this.id = id;
        this.workerRanker = workerRanker;
        this.taskAllocator = taskAllocator;
        this.answerAggregator = answerAggregator;
        this.options = options;
        logger.info(String.format("Allocated a Process called \"%s\" with %d option(s)", id, options.size()));
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public Map<String, String> getOptions() {
        return options;
    }

    @JsonIgnore
    public WorkerRanker getWorkerRanker() {
        return workerRanker;
    }

    @JsonIgnore
    public TaskAllocator getTaskAllocator() {
        return taskAllocator;
    }

    @JsonIgnore
    public AnswerAggregator getAnswerAggregator() {
        return answerAggregator;
    }

    @JsonProperty("workerRanker")
    public String getWorkerRankerName() {
        return workerRanker.getClass().getName();
    }

    @JsonProperty("taskAllocator")
    public String getTaskAllocatorName() {
        return taskAllocator.getClass().getName();
    }

    @JsonProperty("answerAggregator")
    public String getAnswerAggregatorName() {
        return answerAggregator.getClass().getName();
    }
}
