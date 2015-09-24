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
import mtsar.resources.*;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.skife.jdbi.v2.DBI;

import javax.inject.Singleton;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ApplicationModule extends AbstractBinder {
    public final static TypeLiteral<Map<String, Process>> STRING_PROCESS_MAP = new TypeLiteral<Map<String, Process>>() {
    };

    private final DBI jdbi;
    private final Map<String, Process> processes;

    public ApplicationModule(DBI jdbi, Map<String, Process> processes) {
        checkNotNull(this.jdbi = jdbi);
        checkNotNull(this.processes = processes);
    }

    @Override
    protected void configure() {
        bind(jdbi).to(DBI.class);
        bind(jdbi.onDemand(ProcessDAO.class)).to(ProcessDAO.class);
        bind(jdbi.onDemand(WorkerDAO.class)).to(WorkerDAO.class);
        bind(jdbi.onDemand(TaskDAO.class)).to(TaskDAO.class);
        bind(jdbi.onDemand(AnswerDAO.class)).to(AnswerDAO.class);
        bind(processes).to(STRING_PROCESS_MAP).named("processes");

        bind(MetaResource.class).to(MetaResource.class).in(Singleton.class);
        bind(ProcessResource.class).to(ProcessResource.class);
        bind(WorkerResource.class).to(WorkerResource.class);
        bind(TaskResource.class).to(TaskResource.class);
        bind(AnswerResource.class).to(AnswerResource.class);

        bind(MechanicalTsarVersion.class).to(MechanicalTsarVersion.class).in(Singleton.class);
        bind(MechanicalTsarVersionHealthCheck.class).to(MechanicalTsarVersionHealthCheck.class).in(Singleton.class);
    }
}
