package mtsar.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.inferred.freebuilder.FreeBuilder;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@FreeBuilder
@XmlRootElement
@JsonDeserialize(builder = AnswerAggregation.Builder.class)
public interface AnswerAggregation {
    String TYPE_DEFAULT = "aggregation";

    @JsonProperty
    Task getTask();

    @JsonProperty
    List<String> getAnswers();

    @JsonProperty
    default String getType() {
        return TYPE_DEFAULT;
    }

    class Builder extends AnswerAggregation_Builder {
    }
}
