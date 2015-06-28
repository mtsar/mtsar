package mtsar.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import mtsar.api.sql.PostgreSQLTextArray;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;

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

        public Builder setExternalId(String externalId) {
            this.externalId = externalId;
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
        private String externalId;
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
                builder.externalId,
                builder.process,
                builder.description,
                builder.answers,
                builder.dateTime
        );
    }

    protected final Integer id;
    protected final String type;
    protected final String externalId;
    protected final String process;
    protected final String description;
    protected final String[] answers;
    protected final Timestamp dateTime;

    @JsonCreator
    public Task(@JsonProperty("id") Integer id,
                @JsonProperty("type") String type,
                @JsonProperty("externalId") String externalId,
                @JsonProperty("process") String process,
                @JsonProperty("description") String description,
                @JsonProperty("answers") String[] answers,
                @JsonProperty("dateTime") Timestamp dateTime) {
        this.id = id;
        this.type = type;
        this.externalId = externalId;
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

    @JsonProperty
    public String getExternalId() {
        return externalId;
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
        return new PostgreSQLTextArray(answers).toString();
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
                StringUtils.equals(externalId, t.externalId) &&
                StringUtils.equals(process, t.process) &&
                dateTime.equals(t.dateTime);
    }
}
