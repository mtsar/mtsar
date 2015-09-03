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
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import io.dropwizard.setup.Environment;
import mtsar.api.Process;

import javax.validation.Validator;
import java.util.Map;

public class BundleModule extends AbstractModule {
    public final static TypeLiteral<Map<String, mtsar.api.Process>> PROCESSES_TYPE_LITERAL = new TypeLiteral<Map<String, Process>>() {
    };

    private Map<String, Process> processes;

    public BundleModule(Map<String, Process> processes) {
        this.processes = processes;
    }

    @Override
    protected void configure() {
        bind(PROCESSES_TYPE_LITERAL).annotatedWith(Names.named("processes")).toInstance(processes);
    }

    @Provides
    public Validator getValidator(Environment environment) {
        return environment.getValidator();
    }
}
