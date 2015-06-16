package mtsar;

import com.google.common.collect.Maps;
import io.dropwizard.testing.junit.ResourceTestRule;
import mtsar.api.Process;
import mtsar.resources.MetaResource;
import org.glassfish.jersey.test.grizzly.GrizzlyTestContainerFactory;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MetaResourceTest {
    private static final MechanicalTsarVersion version = mock(MechanicalTsarVersion.class);
    private static final Process process = mock(Process.class);

    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
            .setTestContainerFactory(new GrizzlyTestContainerFactory())
            .addResource(new MetaResource(version, Maps.asMap(Sets.newSet("1"), (id) -> process), null, null, null))
            .build();

    @Before
    public void setup() {
        when(version.getVersion()).thenReturn("SNAPSHOT");
    }

    @Test
    public void testGetVersion() {
        assertThat(RULE.getJerseyTest().target("/version").request()
                .get(String.class))
                .isEqualTo("SNAPSHOT");
    }
}
