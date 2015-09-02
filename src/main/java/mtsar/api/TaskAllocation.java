package mtsar.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.inferred.freebuilder.FreeBuilder;

import javax.xml.bind.annotation.XmlRootElement;

@FreeBuilder
@XmlRootElement
@JsonDeserialize(builder = AnswerAggregation.Builder.class)
public interface TaskAllocation {
    String TYPE_DEFAULT = "allocation";

    @JsonProperty
    Worker getWorker();

    @JsonProperty
    Task getTask();

    @JsonProperty
    int getTaskRemaining();

    @JsonProperty
    int getTaskCount();

    @JsonProperty
    default String getType() {
        return TYPE_DEFAULT;
    }

    class Builder extends TaskAllocation_Builder {
    }
}
