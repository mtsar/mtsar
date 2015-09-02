package mtsar.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class AnswerAggregation {
    private final Task task;
    private final List<String> answers;

    @JsonCreator
    public AnswerAggregation(@JsonProperty("task") Task task, @JsonProperty("answers") List<String> answers) {
        this.task = task;
        this.answers = answers;
    }

    @JsonProperty
    public String getType() {
        return getClass().getSimpleName();
    }

    @JsonProperty
    public Task getTask() {
        return task;
    }

    @JsonProperty
    public List<String> getAnswers() {
        return answers;
    }
}
