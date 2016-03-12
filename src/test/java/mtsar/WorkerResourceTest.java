/*
 * Copyright 2016 Dmitry Ustalov
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
import mtsar.api.Answer;
import mtsar.api.Stage;
import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.resources.WorkerResource;
import mtsar.util.PostgresUtils;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.test.grizzly.GrizzlyTestContainerFactory;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static mtsar.TestHelper.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class WorkerResourceTest {
    private static final GenericType<List<Answer>> LIST_ANSWER = new GenericType<List<Answer>>() {
    };

    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final WorkerDAO workerDAO = mock(WorkerDAO.class);
    private static final AnswerDAO answerDAO = mock(AnswerDAO.class);
    private static final Stage stage = mock(Stage.class);

    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
            .setTestContainerFactory(new GrizzlyTestContainerFactory())
            .addProperty("jersey.config.server.provider.classnames", "org.glassfish.jersey.media.multipart.MultiPartFeature")
            .addResource(new WorkerResource(stage, taskDAO, workerDAO, answerDAO))
            .build();

    private static final Task task = fixture("task1.json", Task.class);
    private static final Worker worker = fixture("worker1.json", Worker.class);

    @Before
    public void setup() {
        when(stage.getId()).thenReturn("1");
        when(taskDAO.find(eq(1), eq("1"))).thenReturn(task);
        when(workerDAO.find(eq(1), eq("1"))).thenReturn(worker);
    }

    @Test
    public void testSkipAnswer() {
        reset(answerDAO);
        when(answerDAO.insert(any(Answer.class))).then((invocation) -> {
            final Answer answer = new Answer.Builder().mergeFrom(invocation.getArgumentAt(0, Answer.class)).setId(1).build();
            when(answerDAO.find(eq(answer.getId()), eq(answer.getStage()))).thenReturn(answer);
            return answer.getId();
        });
        final Map<String, String> fixture = fixture("answers1skip.json", PostgresUtils.MAP_STRING_TO_STRING);
        final MultivaluedMap<String, String> entity = new MultivaluedHashMap<>(fixture);
        final Response response = RULE.getJerseyTest().target("/workers/1/answers/skip").request()
                .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
                .method("PATCH", Entity.form(entity));
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        final List<Answer> answers = response.readEntity(LIST_ANSWER);
        assertThat(answers).hasSize(1);
        final Answer answer = answers.get(0);
        assertThat(answer.getWorkerId()).isEqualTo(worker.getId());
        assertThat(answer.getTaskId()).isEqualTo(task.getId());
    }
}
