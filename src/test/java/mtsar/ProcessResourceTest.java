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

package mtsar;

import com.google.common.collect.Maps;
import io.dropwizard.testing.junit.ResourceTestRule;
import mtsar.api.Process;
import mtsar.api.ProcessDefinition;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.TaskAllocator;
import mtsar.processors.WorkerRanker;
import mtsar.resources.ProcessResource;
import org.glassfish.jersey.test.grizzly.GrizzlyTestContainerFactory;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import javax.ws.rs.core.GenericType;
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProcessResourceTest {
    private static final ProcessDefinition definition = mock(ProcessDefinition.class);
    private static final WorkerRanker workerRanker = mock(WorkerRanker.class);
    private static final TaskAllocator taskAllocator = mock(TaskAllocator.class);
    private static final AnswerAggregator answerAggregator = mock(AnswerAggregator.class);
    private static final Process process = new Process(definition, workerRanker, taskAllocator, answerAggregator);

    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
            .setTestContainerFactory(new GrizzlyTestContainerFactory())
            .addResource(new ProcessResource(Maps.asMap(Sets.newSet("1"), (id) -> process), null, null, null))
            .build();

    @Before
    public void setup() {
        when(process.getId()).thenReturn("1");
    }

    @Test
    public void testGetProcesses() {
        final Collection<Map> processes = RULE.getJerseyTest().target("/processes").request().get(new GenericType<Collection<Map>>() {
        });
        assertThat(processes).hasSize(1);
        final Map representation = processes.iterator().next();
        assertThat(representation.get("id")).isEqualTo(process.getId());
    }
}
