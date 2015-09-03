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

import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import mtsar.api.sql.ProcessDAO;
import mtsar.dropwizard.MechanicalTsarApplication;
import mtsar.dropwizard.MechanicalTsarConfiguration;
import net.sourceforge.argparse4j.inf.Namespace;

public class SimulateCommand extends EnvironmentCommand<MechanicalTsarConfiguration> {
    private final MechanicalTsarApplication application;

    public SimulateCommand(Application<MechanicalTsarConfiguration> application) {
        super(application, "simulate", "Runs the simulation");
        this.application = (MechanicalTsarApplication) application;
    }

    protected void run(Environment environment, Namespace namespace, MechanicalTsarConfiguration configuration) {
        final ProcessDAO processDAO = application.getInjector().getInstance(ProcessDAO.class);
    }
}