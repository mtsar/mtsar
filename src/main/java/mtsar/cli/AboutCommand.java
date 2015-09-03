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

package mtsar.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import mtsar.MechanicalTsarVersion;
import mtsar.dropwizard.MechanicalTsarApplication;
import mtsar.dropwizard.MechanicalTsarConfiguration;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;

public class AboutCommand extends EnvironmentCommand<MechanicalTsarConfiguration> {
    private final MechanicalTsarApplication application;

    public AboutCommand(Application<MechanicalTsarConfiguration> application) {
        super(application, "about", "Print the system environment");
        this.application = (MechanicalTsarApplication) application;
    }

    protected void run(Environment environment, Namespace namespace, MechanicalTsarConfiguration configuration) throws IOException {
        final MechanicalTsarVersion version = application.getInjector().getInstance(MechanicalTsarVersion.class);
        System.out.format("Mechanical Tsar version %s%n", version.toString());
        System.out.format("Java version %s%n", System.getProperty("java.runtime.version"));
        System.out.println();
        System.out.format("Configuration: %s%n", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(configuration));
        System.out.println();
        System.out.format("Processes: %s%n", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(application.getProcesses()));
        System.out.flush();
    }
}
