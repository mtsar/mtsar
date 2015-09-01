package mtsar.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.inferred.freebuilder.FreeBuilder;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;

@FreeBuilder
@XmlRootElement
@JsonDeserialize(builder = Event.Builder.class)
public interface Event {
    @JsonProperty("value")
    Integer getValue();

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
    class Builder extends Event_Builder {
        public static final void main(String[] args) throws IOException {
            final Event event1 = new Event.Builder().setValue(1488).build();
            System.out.println(event1.getValue());
            final String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(event1);
            System.out.println(json);
            final Event event2 = new ObjectMapper().readValue(json, Event.class);
            System.out.println(event2.getValue());
            System.gc();
        }
    }
}
