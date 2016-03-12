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
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.resources.MetaResource;
import org.glassfish.jersey.test.grizzly.GrizzlyTestContainerFactory;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class MetaResourceTest {
    private static final MechanicalTsarVersion version = mock(MechanicalTsarVersion.class);
    private static final Stage stage = mock(Stage.class);
    private static final WorkerDAO workerDAO = mock(WorkerDAO.class);
    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final AnswerDAO answerDAO = mock(AnswerDAO.class);

    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
            .setTestContainerFactory(new GrizzlyTestContainerFactory())
            .addResource(new MetaResource(version, Maps.asMap(Sets.newSet("1"), (id) -> stage), taskDAO, workerDAO, answerDAO))
            .addProvider(new ViewMessageBodyWriter(new MetricRegistry(), Collections.singletonList(new MustacheViewRenderer())))
            .build();

    @Before
    public void setup() {
        reset(taskDAO);
        reset(workerDAO);
        reset(answerDAO);
        when(version.getVersion()).thenReturn("SNAPSHOT");
    }

    @Test
    public void testGetDashboard() {
        when(taskDAO.count(anyString())).thenReturn(0);
        when(workerDAO.count(anyString())).thenReturn(0);
        when(answerDAO.count(anyString())).thenReturn(0);
        assertThat(RULE.getJerseyTest().target("/").request()
                .accept(MediaType.TEXT_HTML_TYPE).get().getStatusInfo())
                .isEqualTo(Response.Status.OK);
    }

    @Test
    public void testGetVersion() {
        assertThat(RULE.getJerseyTest().target("/version").request()
                .get(String.class))
                .isEqualTo("SNAPSHOT");
    }
}
