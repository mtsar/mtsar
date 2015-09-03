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

import com.squarespace.jersey2.guice.BootstrapUtils;
import io.dropwizard.testing.junit.DropwizardAppRule;
import mtsar.dropwizard.MechanicalTsarApplication;
import mtsar.dropwizard.MechanicalTsarConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationTest {
    public static final String TEST_YAML_ENV = "MTSAR_TEST_YAML";
    public static final String TEST_YAML = "test.yml";

    @ClassRule
    public static final DropwizardAppRule<MechanicalTsarConfiguration> RULE = new DropwizardAppRule<>(MechanicalTsarApplication.class, StringUtils.defaultString(System.getenv(TEST_YAML_ENV), TEST_YAML));

    @Before
    public void setup() {
        BootstrapUtils.reset();
    }

    @Test
    public void testInitialization() {
        final Client client = new JerseyClientBuilder().build();
        final Response response = client.target(String.format("http://localhost:%d/", RULE.getLocalPort())).request().get();
        assertThat(response.getStatus()).isEqualTo(200);
    }

}
