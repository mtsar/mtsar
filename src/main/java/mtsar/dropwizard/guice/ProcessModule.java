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

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import mtsar.api.ProcessDefinition;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.TaskAllocator;
import mtsar.processors.WorkerRanker;

import java.util.Map;

public class ProcessModule extends AbstractModule {
    public final static TypeLiteral<Map<String, String>> OPTIONS_TYPE_LITERAL = new TypeLiteral<Map<String, String>>() {
    };

    private final String id, description;
    private final Map<String, String> options;
    private final Class<? extends WorkerRanker> workerRanker;
    private final Class<? extends TaskAllocator> taskAllocator;
    private final Class<? extends AnswerAggregator> answerAggregator;

    public ProcessModule(ProcessDefinition definition, Class<? extends WorkerRanker> workerRanker, Class<? extends TaskAllocator> taskAllocator, Class<? extends AnswerAggregator> answerAggregator) {
        this.id = definition.getId();
        this.description = definition.getDescription();
        this.options = definition.getOptions();
        this.workerRanker = workerRanker;
        this.taskAllocator = taskAllocator;
        this.answerAggregator = answerAggregator;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("id")).to(id);
        bindConstant().annotatedWith(Names.named("description")).to(Strings.nullToEmpty(description));
        bind(OPTIONS_TYPE_LITERAL).annotatedWith(Names.named("options")).toInstance(options);
        bind(WorkerRanker.class).to(workerRanker);
        bind(TaskAllocator.class).to(taskAllocator);
        bind(AnswerAggregator.class).to(answerAggregator);
    }
}
