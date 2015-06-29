package mtsar.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import mtsar.api.sql.PostgreSQLTextArray;
import org.apache.commons.lang3.ArrayUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;
import java.util.Optional;

@XmlRootElement
public class Answer {
    public static class Builder {
        public Answer build() {
            return new Answer(this);
        }

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder setProcess(String process) {
            this.process = process;
            return this;
        }

        public Builder setWorkerId(Integer workerId) {
            this.workerId = workerId;
            return this;
        }

        public Builder setTaskId(Integer taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder setAnswer(String answer) {
            this.answers = ArrayUtils.toArray(answer);
            return this;
        }

        public Builder setAnswers(String[] answers) {
            this.answers = answers;
            return this;
        }

        public Builder setDateTime(Timestamp dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        private Integer id;
        private String externalId;
        private String process;
        private Integer workerId;
        private Integer taskId;
        private String[] answers;
        private Timestamp dateTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    private Answer(Builder builder) {
        this(
                builder.id,
                builder.externalId,
                builder.process,
                builder.workerId,
                builder.taskId,
                builder.answers,
                builder.dateTime
        );
    }

    protected final Integer id;
    protected final String externalId;
    protected final String process;
    protected final Integer workerId;
    protected final Integer taskId;
    protected final String[] answers;
    protected final Timestamp dateTime;

    @JsonCreator
    public Answer(@JsonProperty("id") Integer id,
                  @JsonProperty("externalId") String externalId,
                  @JsonProperty("process") String process,
                  @JsonProperty("workerId") Integer workerId,
                  @JsonProperty("taskId") Integer taskId,
                  @JsonProperty("answer") String[] answers,
                  @JsonProperty("dateTime") Timestamp dateTime) {
        this.id = id;
        this.externalId = externalId;
        this.process = process;
        this.workerId = workerId;
        this.taskId = taskId;
        this.answers = answers;
        this.dateTime = dateTime;
    }

    @JsonProperty
    public Integer getId() {
        return id;
    }

    @JsonProperty
    public String getExternalId() {
        return externalId;
    }

    @JsonProperty
    public String getProcess() {
        return process;
    }

    @JsonProperty
    public Integer getWorkerId() {
        return workerId;
    }

    @JsonProperty
    public Integer getTaskId() {
        return taskId;
    }

    @JsonIgnore
    public Optional<String> getAnswer() {
        if (ArrayUtils.isEmpty(answers)) return Optional.empty();
        return Optional.of(answers[0]);
    }

    @JsonProperty
    public String[] getAnswers() {
        return answers;
    }

    @JsonIgnore
    public String getAnswersTextArray() {
        return new PostgreSQLTextArray(answers).toString();
    }

    @JsonProperty
    public Timestamp getDateTime() {
        return dateTime;
    }
}
