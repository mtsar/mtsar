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
import mtsar.processors.AnswerAggregator;
import mtsar.processors.TaskAllocator;
import mtsar.processors.WorkerRanker;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
@XmlRootElement
public class Process {
    protected final ProcessDefinition definition;
    protected final WorkerRanker workerRanker;
    protected final TaskAllocator taskAllocator;
    protected final AnswerAggregator answerAggregator;

    @Inject
    public Process(ProcessDefinition definition, WorkerRanker workerRanker, TaskAllocator taskAllocator, AnswerAggregator answerAggregator) {
        checkNotNull(this.definition = definition);
        checkNotNull(this.workerRanker = workerRanker);
        checkNotNull(this.taskAllocator = taskAllocator);
        checkNotNull(this.answerAggregator = answerAggregator);
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
}
