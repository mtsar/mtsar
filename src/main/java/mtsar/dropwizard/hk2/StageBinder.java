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

import mtsar.api.Stage;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.TaskAllocator;
import mtsar.processors.WorkerRanker;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

import static java.util.Objects.requireNonNull;

public class StageBinder extends AbstractBinder {
    private final Stage.Definition definition;
    private final Class<? extends WorkerRanker> workerRankerClass;
    private final Class<? extends TaskAllocator> taskAllocatorClass;
    private final Class<? extends AnswerAggregator> answerAggregatorClass;

    public StageBinder(Stage.Definition definition, Class<? extends WorkerRanker> workerRankerClass, Class<? extends TaskAllocator> taskAllocatorClass, Class<? extends AnswerAggregator> answerAggregatorClass) {
        this.definition = requireNonNull(definition);
        this.workerRankerClass = requireNonNull(workerRankerClass);
        this.taskAllocatorClass = requireNonNull(taskAllocatorClass);
        this.answerAggregatorClass = requireNonNull(answerAggregatorClass);
    }

    @Override
    protected void configure() {
        bind(definition).to(Stage.Definition.class);
        bind(workerRankerClass).to(WorkerRanker.class).in(Singleton.class);
        bind(taskAllocatorClass).to(TaskAllocator.class).in(Singleton.class);
        bind(answerAggregatorClass).to(AnswerAggregator.class).in(Singleton.class);
        bindAsContract(Stage.class).in(Singleton.class);
    }
}
