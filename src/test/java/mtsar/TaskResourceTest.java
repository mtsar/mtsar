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

import io.dropwizard.testing.junit.ResourceTestRule;
import mtsar.api.Stage;
import mtsar.api.Task;
import mtsar.api.sql.TaskDAO;
import mtsar.resources.TaskResource;
import org.glassfish.jersey.test.grizzly.GrizzlyTestContainerFactory;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static mtsar.TestHelper.fixture;
import static mtsar.TestHelper.params;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TaskResourceTest {
    private static final TaskDAO dao = mock(TaskDAO.class);
    private static final Stage stage = mock(Stage.class);

    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
            .setTestContainerFactory(new GrizzlyTestContainerFactory())
            .addProperty("jersey.config.server.provider.classnames", "org.glassfish.jersey.media.multipart.MultiPartFeature")
            .addResource(new TaskResource(stage, dao, null, null))
            .build();

    private static final Task task = fixture("task1.json", Task.class);

    @Before
    public void setup() {
        when(stage.getId()).thenReturn("1");
        when(dao.find(eq(1), eq("1"))).thenReturn(task);
    }

    @Test
    public void testGetTask() {
        assertThat(RULE.getJerseyTest().target("/tasks/1").request()
                .get(Task.class))
                .isEqualTo(task);
        verify(dao).find(1, "1");
    }

    @Test
    public void testPostTask() {
        reset(dao);
        when(dao.insert(any(Task.class))).then((invocation) -> {
            final Task task = new Task.Builder().mergeFrom(invocation.getArgumentAt(0, Task.class)).setId(2).build();
            when(dao.find(eq(task.getId()), eq(task.getStage()))).thenReturn(task);
            return task.getId();
        });
        assertThat(RULE.getJerseyTest().target("/tasks").request()
                .post(Entity.form(params(task))).getStatusInfo())
                .isEqualTo(Response.Status.CREATED);
    }
}
