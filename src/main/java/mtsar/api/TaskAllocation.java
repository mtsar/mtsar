/*
 * Copyright 2015 Dmitry Ustalov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mtsar.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.inferred.freebuilder.FreeBuilder;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Optional;

@FreeBuilder
@XmlRootElement
@JsonDeserialize(builder = TaskAllocation.Builder.class)
public interface TaskAllocation {
    String TYPE_DEFAULT = "allocation";

    @JsonProperty
    Worker getWorker();

    @JsonProperty
    List<Task> getTasks();

    @JsonProperty
    int getTaskRemaining();

    @JsonProperty
    int getTaskCount();

    @JsonProperty
    String getType();

    @JsonIgnore
    default Optional<Task> getTask() {
        if (getTasks().isEmpty()) return Optional.empty();
        return Optional.of(getTasks().get(0));
    }

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
    class Builder extends TaskAllocation_Builder {
        public Builder() {
            setType(TYPE_DEFAULT);
        }
    }
}
