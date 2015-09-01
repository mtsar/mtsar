package mtsar.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import mtsar.DefaultDateTime;
import mtsar.api.sql.PostgreSQLTextArray;
import org.inferred.freebuilder.FreeBuilder;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;
import java.util.List;

@FreeBuilder
@XmlRootElement
@JsonDeserialize(builder = Worker.Builder.class)
public interface Worker {
    @Nullable
    @JsonProperty
    Integer getId();

    @JsonProperty
    String getProcess();

    @JsonProperty
    Timestamp getDateTime();

    @JsonProperty
    List<String> getTags();

    @JsonIgnore
    String getTagsTextArray();

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
    class Builder extends Worker_Builder {
        public Builder() {
            setDateTime(DefaultDateTime.get());
        }

        public Worker build() {
            setTagsTextArray(new PostgreSQLTextArray(getTags()).toString());
            return super.build();
        }
    }
}
