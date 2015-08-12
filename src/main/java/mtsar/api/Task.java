package mtsar.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import mtsar.api.sql.PostgreSQLTextArray;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;

@XmlRootElement
public class Task {
    public static class Builder {
        public Task build() {
            return new Task(this);
        }

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
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

        public Builder setDescription(String description) {
            this.description = description;
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
        private String type;
        private String[] tags;
        private String process;
        private String description;
        private String[] answers;
        private Timestamp dateTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    private Task(Builder builder) {
        this(
                builder.id,
                builder.type,
                builder.tags,
                builder.process,
                builder.description,
                builder.answers,
                builder.dateTime
        );
    }

    protected final Integer id;
    protected final String type;
    protected final String[] tags;
    protected final String process;
    protected final String description;
    protected final String[] answers;
    protected final Timestamp dateTime;

    @JsonCreator
    public Task(@JsonProperty("id") Integer id,
                @JsonProperty("type") String type,
                @JsonProperty("tags") String[] tags,
                @JsonProperty("process") String process,
                @JsonProperty("description") String description,
                @JsonProperty("answers") String[] answers,
                @JsonProperty("dateTime") Timestamp dateTime) {
        this.id = id;
        this.type = type;
        this.tags = tags;
        this.process = process;
        this.description = description;
        this.answers = answers;
        this.dateTime = dateTime;
    }

    @JsonProperty
    public Integer getId() {
        return id;
    }

    @JsonProperty
    public String getType() {
        return type;
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
    public String getDescription() {
        return description;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Task t = (Task) obj;

        return id.equals(t.id) &&
                Objects.deepEquals(tags, t.tags) &&
                StringUtils.equals(process, t.process) &&
                dateTime.equals(t.dateTime);
    }
}
