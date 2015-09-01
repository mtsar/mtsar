package mtsar;

import io.dropwizard.testing.junit.ResourceTestRule;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.sql.TaskDAO;
import mtsar.resources.TaskResource;
import org.glassfish.jersey.test.grizzly.GrizzlyTestContainerFactory;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TaskResourceTest {
    private static final TaskDAO dao = mock(TaskDAO.class);
    private static final Process process = mock(Process.class);

    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
            .setTestContainerFactory(new GrizzlyTestContainerFactory())
            .addProperty("jersey.config.server.provider.classnames", "org.glassfish.jersey.media.multipart.MultiPartFeature")
            .addResource(new TaskResource(process, dao, null, null))
            .build();

    private final Task task = new Task.Builder().
            setId(1).
            setProcess("1").
            setDescription("Test example").
            setType("single").
            build();

    @Before
    public void setup() {
        when(process.getId()).thenReturn("1");
        when(dao.find(eq(1), eq("1"))).thenReturn(task);
    }

    @Test
    public void testGetTask() {
        assertThat(RULE.getJerseyTest().target("/tasks/1").request()
                .get(Task.class))
                .isEqualTo(task);
        verify(dao).find(1, "1");
    }
}
