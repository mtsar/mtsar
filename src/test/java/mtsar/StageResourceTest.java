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

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Maps;
import io.dropwizard.testing.junit.ResourceTestRule;
import io.dropwizard.views.ViewMessageBodyWriter;
import io.dropwizard.views.mustache.MustacheViewRenderer;
import mtsar.api.Stage;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.StageDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.dropwizard.hk2.StagesService;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.TaskAllocator;
import mtsar.processors.WorkerRanker;
import mtsar.resources.StageResource;
import org.glassfish.jersey.test.grizzly.GrizzlyTestContainerFactory;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class StageResourceTest {
    private static final GenericType<Collection<Map>> MAP_COLLECTION = new GenericType<Collection<Map>>() {
    };

    private static final Stage.Definition definition = mock(Stage.Definition.class);
    private static final WorkerRanker workerRanker = mock(WorkerRanker.class);
    private static final TaskAllocator taskAllocator = mock(TaskAllocator.class);
    private static final AnswerAggregator answerAggregator = mock(AnswerAggregator.class);
    private static final Stage stage = new Stage(definition, workerRanker, taskAllocator, answerAggregator);
    private static final WorkerDAO workerDAO = mock(WorkerDAO.class);
    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final AnswerDAO answerDAO = mock(AnswerDAO.class);
    private static final StageDAO stageDAO = mock(StageDAO.class);
    private static final StagesService stagesService = mock(StagesService.class);

    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
            .setTestContainerFactory(new GrizzlyTestContainerFactory())
            .addResource(new StageResource(stagesService, taskDAO, workerDAO, answerDAO, stageDAO))
            .addProvider(new ViewMessageBodyWriter(new MetricRegistry(), Collections.singletonList(new MustacheViewRenderer())))
            .build();

    @Before
    public void setup() {
        reset(taskDAO);
        reset(workerDAO);
        reset(answerDAO);
        when(stage.getId()).thenReturn("1");
        when(stagesService.getStages()).thenReturn(Maps.asMap(Sets.newSet("1"), (id) -> stage));
    }

    @Test
    public void testGetStages() {
        final Collection<Map> stages = RULE.getJerseyTest().target("/stages").request()
                .accept(MediaType.APPLICATION_JSON_TYPE).get(MAP_COLLECTION);
        assertThat(stages).hasSize(1);
        final Map representation = stages.iterator().next();
        assertThat(representation.get("id")).isEqualTo(stage.getId());
    }

    @Test
    public void testGetStagesView() {
        when(taskDAO.count(anyString())).thenReturn(0);
        when(workerDAO.count(anyString())).thenReturn(0);
        when(answerDAO.count(anyString())).thenReturn(0);
        assertThat(RULE.getJerseyTest().target("/stages").request()
                .accept(MediaType.TEXT_HTML_TYPE).get().getStatusInfo())
                .isEqualTo(Response.Status.OK);
    }
}
