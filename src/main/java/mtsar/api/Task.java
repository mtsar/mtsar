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
@JsonDeserialize(builder = Task.Builder.class)
public interface Task {
    @Nullable
    @JsonProperty
    Integer getId();

    @JsonProperty
    String getProcess();

    @JsonProperty
    Timestamp getDateTime();

    @JsonProperty
    List<String> getTags();

    @JsonProperty
    String getType();

    @JsonProperty
    String getDescription();

    @JsonProperty
    List<String> getAnswers();

    @JsonIgnore
    String getTagsTextArray();

    @JsonIgnore
    String getAnswersTextArray();

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
    class Builder extends Task_Builder {
        public Builder() {
            setDateTime(DefaultDateTime.get());
        }

        public Task build() {
            setTagsTextArray(new PostgreSQLTextArray(getTags()).toString());
            setAnswersTextArray(new PostgreSQLTextArray(getAnswers()).toString());
            return super.build();
        }
    }
}
