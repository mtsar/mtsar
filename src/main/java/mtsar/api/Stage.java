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
import mtsar.processors.AnswerAggregator;
import mtsar.processors.TaskAllocator;
import mtsar.processors.WorkerRanker;
import mtsar.util.DateTimeUtils;
import mtsar.util.PostgresUtils;
import org.inferred.freebuilder.FreeBuilder;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@XmlRootElement
public class Stage {
    private final Definition definition;
    private final WorkerRanker workerRanker;
    private final TaskAllocator taskAllocator;
    private final AnswerAggregator answerAggregator;

    @Inject
    public Stage(Definition definition, WorkerRanker workerRanker, TaskAllocator taskAllocator, AnswerAggregator answerAggregator) {
        this.definition = requireNonNull(definition);
        this.workerRanker = requireNonNull(workerRanker);
        this.taskAllocator = requireNonNull(taskAllocator);
        this.answerAggregator = requireNonNull(answerAggregator);
    }

    @JsonProperty
    public String getId() {
        return definition.getId();
    }

    @JsonProperty
    public String getDescription() {
        return definition.getDescription();
    }

    @JsonProperty
    public Map<String, String> getOptions() {
        return definition.getOptions();
    }

    @JsonIgnore
    public WorkerRanker getWorkerRanker() {
        return workerRanker;
    }

    @JsonIgnore
    public TaskAllocator getTaskAllocator() {
        return taskAllocator;
    }

    @JsonIgnore
    public AnswerAggregator getAnswerAggregator() {
        return answerAggregator;
    }

    @JsonProperty("workerRanker")
    @SuppressWarnings("unused")
    public String getWorkerRankerName() {
        return definition.getWorkerRanker();
    }

    @JsonProperty("taskAllocator")
    @SuppressWarnings("unused")
    public String getTaskAllocatorName() {
        return definition.getTaskAllocator();
    }

    @JsonProperty("answerAggregator")
    @SuppressWarnings("unused")
    public String getAnswerAggregatorName() {
        return definition.getAnswerAggregator();
    }

    @FreeBuilder
    @XmlRootElement
    @JsonDeserialize(builder = Definition.Builder.class)
    public interface Definition {
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
        class Builder extends Stage_Definition_Builder {
            public Builder() {
                setDateTime(DateTimeUtils.now());
            }

            public Builder setOptions(String json) {
                return putAllOptions(PostgresUtils.parseJSONString(json));
            }

            @Override
            public Definition build() {
                setOptionsJSON(PostgresUtils.buildJSONString(getOptions()));
                return super.build();
            }
        }
    }
}
