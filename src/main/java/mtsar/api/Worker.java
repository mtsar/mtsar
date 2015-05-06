package mtsar.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;

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

        public Builder setExternalId(String externalId) {
            this.externalId = externalId;
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
        private String externalId;
        private String process;
        private Timestamp dateTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    private Worker(Builder builder) {
        this(builder.id, builder.externalId, builder.process, builder.dateTime);
    }

    protected final Integer id;
    protected final String externalId;
    protected final String process;
    protected final Timestamp dateTime;

    @JsonCreator
    public Worker(@JsonProperty("id") Integer id,
                  @JsonProperty("externalId") String externalId,
                  @JsonProperty("process") String process,
                  @JsonProperty("dateTime") Timestamp dateTime) {
        this.id = id;
        this.externalId = externalId;
        this.process = process;
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
    public Timestamp getDateTime() {
        return dateTime;
    }
}
