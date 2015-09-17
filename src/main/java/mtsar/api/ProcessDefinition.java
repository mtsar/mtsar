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
import mtsar.PostgresUtils;
import org.inferred.freebuilder.FreeBuilder;

import javax.xml.bind.annotation.XmlRootElement;
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
        public Builder setOptions(String json) {
            return putAllOptions(PostgresUtils.parseJSONString(json));
        }

        @Override
        public ProcessDefinition build() {
            setOptionsJSON(PostgresUtils.buildJSONString(getOptions()));
            return super.build();
        }
    }
}
