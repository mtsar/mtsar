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

package mtsar.dropwizard.hk2;

import mtsar.MechanicalTsarVersion;
import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.ProcessDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.dropwizard.MechanicalTsarVersionHealthCheck;
import mtsar.resources.MetaResource;
import mtsar.resources.ProcessResource;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.skife.jdbi.v2.DBI;

import javax.inject.Singleton;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class ApplicationBinder extends AbstractBinder {
    public final static TypeLiteral<Map<String, Process>> STRING_PROCESS_MAP = new TypeLiteral<Map<String, Process>>() {
    };

    private final DBI jdbi;
    private final Map<String, Process> processes;

    public ApplicationBinder(DBI jdbi, Map<String, Process> processes) {
        this.jdbi = requireNonNull(jdbi);
        this.processes = requireNonNull(processes);
    }

    @Override
    protected void configure() {
        bind(jdbi).to(DBI.class);
        bind(jdbi.onDemand(ProcessDAO.class)).to(ProcessDAO.class);
        bind(jdbi.onDemand(WorkerDAO.class)).to(WorkerDAO.class);
        bind(jdbi.onDemand(TaskDAO.class)).to(TaskDAO.class);
        bind(jdbi.onDemand(AnswerDAO.class)).to(AnswerDAO.class);
        bind(processes).to(STRING_PROCESS_MAP).named("processes");

        bindAsContract(MetaResource.class).in(Singleton.class);
        bindAsContract(ProcessResource.class).in(Singleton.class);

        bindAsContract(MechanicalTsarVersion.class).in(Singleton.class);
        bindAsContract(MechanicalTsarVersionHealthCheck.class).in(Singleton.class);
    }
}
