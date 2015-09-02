package mtsar.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.inferred.freebuilder.FreeBuilder;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

@FreeBuilder
@XmlRootElement
@JsonDeserialize(builder = ProcessDefinition.Builder.class)
public interface ProcessDefinition {
    @JsonProperty
    String getId();

    @JsonProperty
    Timestamp getDateTime();

    @JsonProperty
    String getDescription();

    @JsonProperty()
    String getWorkerRanker();

    @JsonProperty()
    String getTaskAllocator();

    @JsonProperty()
    String getAnswerAggregator();

    @JsonProperty
    Map<String, String> getOptions();

    @JsonIgnore
    String getOptionsJSON();

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
    class Builder extends ProcessDefinition_Builder {
        public Builder setOptions(String optionsJSON) {
            try {
                return putAllOptions(new ObjectMapper().readValue(optionsJSON, MAP_STRING_TO_STRING));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public ProcessDefinition build() {
            try {
                setOptionsJSON(new ObjectMapper().writeValueAsString(getOptions()));
            } catch (JsonProcessingException e) {
                setOptionsJSON("{}");
                throw new RuntimeException(e);
            }
            return super.build();
        }
    }

    TypeReference<Map<String, String>> MAP_STRING_TO_STRING = new TypeReference<Map<String, String>>() {
    };
}
