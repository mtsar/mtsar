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
import mtsar.api.Stage;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.StageDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.dropwizard.MechanicalTsarVersionHealthCheck;
import mtsar.resources.MetaResource;
import mtsar.resources.StageResource;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.skife.jdbi.v2.DBI;

import javax.inject.Singleton;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class ApplicationBinder extends AbstractBinder {
    private final static TypeLiteral<Map<String, Stage>> STRING_STAGE_MAP = new TypeLiteral<Map<String, Stage>>() {
    };

    private final DBI jdbi;
    private final Map<String, Stage> stages;

    public ApplicationBinder(DBI jdbi, Map<String, Stage> stages) {
        this.jdbi = requireNonNull(jdbi);
        this.stages = requireNonNull(stages);
    }

    @Override
    protected void configure() {
        bind(jdbi).to(DBI.class);
        bind(jdbi.onDemand(StageDAO.class)).to(StageDAO.class);
        bind(jdbi.onDemand(WorkerDAO.class)).to(WorkerDAO.class);
        bind(jdbi.onDemand(TaskDAO.class)).to(TaskDAO.class);
        bind(jdbi.onDemand(AnswerDAO.class)).to(AnswerDAO.class);
        bind(stages).to(STRING_STAGE_MAP).named("stages");

        bindAsContract(MetaResource.class).in(Singleton.class);
        bindAsContract(StageResource.class).in(Singleton.class);

        bindAsContract(MechanicalTsarVersion.class).in(Singleton.class);
        bindAsContract(MechanicalTsarVersionHealthCheck.class).in(Singleton.class);
    }
}
