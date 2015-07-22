package mtsar;

import com.google.common.collect.Maps;
import io.dropwizard.testing.junit.ResourceTestRule;
import mtsar.api.Process;
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
    private static final mtsar.api.Process process = mock(Process.class);

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
