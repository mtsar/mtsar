package mtsar.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Injector;
import mtsar.api.jdbi.AnswerDAO;
import mtsar.api.jdbi.EventDAO;
import mtsar.api.jdbi.TaskDAO;
import mtsar.api.jdbi.WorkerDAO;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.Processor;
import mtsar.processors.TaskAllocator;
import mtsar.processors.WorkerRanker;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.Map;

@XmlRootElement
public class Process {
    protected String id;
    @Valid
    @NotNull
    protected String workerRankerName, taskAllocatorName, answerAggregatorName;
    protected WorkerRanker workerRanker = null;
    protected TaskAllocator taskAllocator = null;
    protected AnswerAggregator answerAggregator = null;
    protected Injector injector = null;
    protected WorkerDAO workerDAO = null;
    protected TaskDAO taskDAO = null;
    protected AnswerDAO answerDAO = null;
    protected EventDAO eventDAO = null;
    protected Map<String, Object> options = Collections.emptyMap();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("workerRanker")
    public String getWorkerRankerName() {
        return workerRankerName;
    }

    @JsonProperty("workerRanker")
    public void setWorkerRankerName(String workerRankerName) {
        this.workerRankerName = workerRankerName;
    }

    @JsonIgnore
    public WorkerRanker getWorkerRanker() {
        return workerRanker;
    }

    @JsonProperty("taskAllocator")
    public String getTaskAllocatorName() {
        return taskAllocatorName;
    }

    @JsonProperty("taskAllocator")
    public void setTaskAllocatorName(String taskAllocatorName) {
        this.taskAllocatorName = taskAllocatorName;
    }

    @JsonIgnore
    public TaskAllocator getTaskAllocator() {
        return taskAllocator;
    }

    @JsonProperty("answerAggregator")
    public String getAnswerAggregatorName() {
        return answerAggregatorName;
    }

    @JsonProperty("answerAggregator")
    public void setAnswerAggregatorName(String answerAggregatorName) {
        this.answerAggregatorName = answerAggregatorName;
    }

    @JsonProperty
    public Map<String, Object> getOptions() {
        return options;
    }

    @JsonProperty
    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    @JsonIgnore
    public AnswerAggregator getAnswerAggregator() {
        return answerAggregator;
    }

    @JsonIgnore
    public WorkerDAO getWorkerDAO() {
        return workerDAO;
    }

    @JsonIgnore
    public TaskDAO getTaskDAO() {
        return taskDAO;
    }

    @JsonIgnore
    public AnswerDAO getAnswerDAO() {
        return answerDAO;
    }

    @JsonIgnore
    public EventDAO getEventDAO() {
        return eventDAO;
    }

    public void bootstrap(final Injector injector) throws ClassNotFoundException {
        this.injector = injector;
        workerDAO = injector.getInstance(WorkerDAO.class);
        taskDAO = injector.getInstance(TaskDAO.class);
        answerDAO = injector.getInstance(AnswerDAO.class);
        eventDAO = injector.getInstance(EventDAO.class);
        workerRanker = (WorkerRanker) injector.getInstance(Class.forName(workerRankerName));
        taskAllocator = (TaskAllocator) injector.getInstance(Class.forName(taskAllocatorName));
        answerAggregator = (AnswerAggregator) injector.getInstance(Class.forName(answerAggregatorName));
        ((Processor) workerRanker).setProcess(this);
        ((Processor) taskAllocator).setProcess(this);
        ((Processor) answerAggregator).setProcess(this);
    }
}
