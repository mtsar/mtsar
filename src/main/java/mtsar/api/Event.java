package mtsar.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.inferred.freebuilder.FreeBuilder;

import javax.xml.bind.annotation.XmlRootElement;

@FreeBuilder
@XmlRootElement
@JsonDeserialize(builder = Event.Builder.class)
public interface Event {
    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
    class Builder extends Event_Builder {
    }
}
