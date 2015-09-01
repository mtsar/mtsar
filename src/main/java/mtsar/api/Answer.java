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
import java.util.Optional;

@FreeBuilder
@XmlRootElement
@JsonDeserialize(builder = Answer.Builder.class)
public interface Answer {
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
    Integer getWorkerId();

    @JsonProperty
    Integer getTaskId();

    @JsonProperty
    List<String> getAnswers();

    @JsonIgnore
    default Optional<String> getAnswer() {
        if (getAnswers().isEmpty()) return Optional.empty();
        return Optional.of(getAnswers().get(0));
    }

    @JsonIgnore
    String getTagsTextArray();

    @JsonIgnore
    String getAnswersTextArray();

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
    class Builder extends Answer_Builder {
        public Builder() {
            setDateTime(DefaultDateTime.get());
        }

        public Answer build() {
            setTagsTextArray(new PostgreSQLTextArray(getTags()).toString());
            setAnswersTextArray(new PostgreSQLTextArray(getAnswers()).toString());
            return super.build();
        }
    }
}
