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

        public Builder setTags(String[] tags) {
            this.tags = tags;
            return this;
        }

        public Builder setProcess(String process) {
            this.process = process;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
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
        private String[] tags;
        private String process;
        private String type;
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
                builder.tags,
                builder.process,
                builder.type,
                builder.workerId,
                builder.taskId,
                builder.answers,
                builder.dateTime
        );
    }

    protected final Integer id;
    protected final String[] tags;
    protected final String process;
    protected final String type;
    protected final Integer workerId;
    protected final Integer taskId;
    protected final String[] answers;
    protected final Timestamp dateTime;

    @JsonCreator
    public Answer(@JsonProperty("id") Integer id,
                  @JsonProperty("tags") String[] tags,
                  @JsonProperty("process") String process,
                  @JsonProperty("type") String type,
                  @JsonProperty("workerId") Integer workerId,
                  @JsonProperty("taskId") Integer taskId,
                  @JsonProperty("answer") String[] answers,
                  @JsonProperty("dateTime") Timestamp dateTime) {
        this.id = id;
        this.tags = tags;
        this.process = process;
        this.type = type;
        this.workerId = workerId;
        this.taskId = taskId;
        this.answers = answers;
        this.dateTime = dateTime;
    }

    @JsonProperty
    public Integer getId() {
        return id;
    }

    @JsonIgnore
    public Optional<String> getTag() {
        if (ArrayUtils.isEmpty(tags)) return Optional.empty();
        return Optional.of(tags[0]);
    }

    @JsonProperty
    public String[] getTags() {
        return tags;
    }

    @JsonIgnore
    public String getTagsTextArray() {
        return new PostgreSQLTextArray(ArrayUtils.nullToEmpty(tags)).toString();
    }

    @JsonProperty
    public String getProcess() {
        return process;
    }

    @JsonProperty
    public String getType() {
        return type;
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
        return new PostgreSQLTextArray(ArrayUtils.nullToEmpty(answers)).toString();
    }

    @JsonProperty
    public Timestamp getDateTime() {
        return dateTime;
    }
}
