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
import mtsar.api.Stage;
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
    private static final Stage stage = mock(Stage.class);

    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
            .setTestContainerFactory(new GrizzlyTestContainerFactory())
            .addResource(new MetaResource(version, Maps.asMap(Sets.newSet("1"), (id) -> stage), null, null, null))
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
