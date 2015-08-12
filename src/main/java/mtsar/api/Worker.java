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
public class Worker {
    public static class Builder {
        public Worker build() {
            return new Worker(this);
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

        public Builder setDateTime(Timestamp dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        private Integer id;
        private String[] tags;
        private String process;
        private Timestamp dateTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    private Worker(Builder builder) {
        this(builder.id, builder.tags, builder.process, builder.dateTime);
    }

    protected final Integer id;
    protected final String[] tags;
    protected final String process;
    protected final Timestamp dateTime;

    @JsonCreator
    public Worker(@JsonProperty("id") Integer id,
                  @JsonProperty("tags") String[] tags,
                  @JsonProperty("process") String process,
                  @JsonProperty("dateTime") Timestamp dateTime) {
        this.id = id;
        this.tags = tags;
        this.process = process;
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
    public Timestamp getDateTime() {
        return dateTime;
    }
}
