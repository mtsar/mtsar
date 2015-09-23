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

package mtsar.dropwizard.guice;

import com.google.inject.AbstractModule;
import mtsar.api.ProcessDefinition;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.TaskAllocator;
import mtsar.processors.WorkerRanker;

public class ProcessModule extends AbstractModule {
    private final ProcessDefinition definition;
    private final Class<? extends WorkerRanker> workerRanker;
    private final Class<? extends TaskAllocator> taskAllocator;
    private final Class<? extends AnswerAggregator> answerAggregator;

    public ProcessModule(ProcessDefinition definition, Class<? extends WorkerRanker> workerRanker, Class<? extends TaskAllocator> taskAllocator, Class<? extends AnswerAggregator> answerAggregator) {
        this.definition = definition;
        this.workerRanker = workerRanker;
        this.taskAllocator = taskAllocator;
        this.answerAggregator = answerAggregator;
    }

    @Override
    protected void configure() {
        bind(ProcessDefinition.class).toInstance(definition);
        bind(WorkerRanker.class).to(workerRanker);
        bind(TaskAllocator.class).to(taskAllocator);
        bind(AnswerAggregator.class).to(answerAggregator);
    }
}
