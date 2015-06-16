package mtsar;

import com.google.common.collect.Maps;
import io.dropwizard.testing.junit.ResourceTestRule;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.jdbi.TaskDAO;
import mtsar.resources.ProcessResource;
import org.glassfish.jersey.test.grizzly.GrizzlyTestContainerFactory;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TaskResourceTest {
    private static final TaskDAO dao = mock(TaskDAO.class);
    private static final Process process = mock(Process.class);

    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
            .setTestContainerFactory(new GrizzlyTestContainerFactory())
            .addResource(new ProcessResource(Maps.asMap(Sets.newSet("1"), (id) -> process), dao, null, null))
            .build();

    private final Task task = Task.builder().
            setId(1).
            setProcess("1").
            setDateTime(Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())).
            build();

    @Before
    public void setup() {
        when(process.getId()).thenReturn("1");
        when(dao.find(eq(1), eq("1"))).thenReturn(task);
    }

    @Test
    public void testGetTask() {
        assertThat(RULE.getJerseyTest().target("/processes/1/tasks/1").request()
                .get(Task.class))
                .isEqualTo(task);
        verify(dao).find(1, "1");
    }
}
