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

package mtsar.dropwizard.hk2;

import mtsar.api.Process;
import mtsar.api.ProcessDefinition;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.TaskAllocator;
import mtsar.processors.WorkerRanker;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

public class ProcessBinder extends AbstractBinder {
    private final ProcessDefinition definition;
    private final Class<? extends WorkerRanker> workerRankerClass;
    private final Class<? extends TaskAllocator> taskAllocatorClass;
    private final Class<? extends AnswerAggregator> answerAggregatorClass;

    public ProcessBinder(ProcessDefinition definition, Class<? extends WorkerRanker> workerRankerClass, Class<? extends TaskAllocator> taskAllocatorClass, Class<? extends AnswerAggregator> answerAggregatorClass) {
        checkNotNull(this.definition = definition);
        checkNotNull(this.workerRankerClass = workerRankerClass);
        checkNotNull(this.taskAllocatorClass = taskAllocatorClass);
        checkNotNull(this.answerAggregatorClass = answerAggregatorClass);
    }

    @Override
    protected void configure() {
        bind(definition).to(ProcessDefinition.class);
        bind(workerRankerClass).to(WorkerRanker.class).in(Singleton.class);
        bind(taskAllocatorClass).to(TaskAllocator.class).in(Singleton.class);
        bind(answerAggregatorClass).to(AnswerAggregator.class).in(Singleton.class);
        bind(Process.class).to(Process.class).in(Singleton.class);
    }
}
