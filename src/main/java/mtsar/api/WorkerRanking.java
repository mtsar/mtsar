package mtsar.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.inferred.freebuilder.FreeBuilder;

import javax.xml.bind.annotation.XmlRootElement;

@FreeBuilder
@XmlRootElement
@JsonDeserialize(builder = WorkerRanking.Builder.class)
public interface WorkerRanking {
    String TYPE_DEFAULT = "ranking";

    @JsonProperty
    Worker getWorker();

    @JsonProperty
    Double getReputation();

    @JsonProperty
    default String getType() {
        return TYPE_DEFAULT;
    }

    class Builder extends WorkerRanking_Builder {
        public Builder() {
            setReputation(0.0);
        }
    }
}
