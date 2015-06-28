package mtsar.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AnswerAggregation {
    private final Task task;
    private final Answer answer;

    @JsonCreator
    public AnswerAggregation(@JsonProperty("task") Task task, @JsonProperty("answer") Answer answer) {
        this.task = task;
        this.answer = answer;
    }

    @JsonProperty
    public Task getTask() {
        return task;
    }

    @JsonProperty
    public Answer getAnswer() {
        return answer;
    }
}
