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

import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import mtsar.MechanicalTsarVersion;
import mtsar.api.Stage;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.StageDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.dropwizard.MechanicalTsarConfiguration;
import mtsar.dropwizard.MechanicalTsarVersionHealthCheck;
import mtsar.resources.MetaResource;
import mtsar.resources.StageResource;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.internal.inject.Injections;
import org.skife.jdbi.v2.DBI;

import javax.inject.Singleton;
import java.util.Map;

public class ApplicationBinder extends AbstractBinder {
    private final DBI jdbi;
    private final ServiceLocator locator;

    public ServiceLocator getLocator() {
        return locator;
    }

    public ApplicationBinder(MechanicalTsarConfiguration configuration, Environment environment) {
        jdbi = new DBIFactory().build(environment, configuration.getDataSourceFactory(), "postgresql");
        locator = Injections.createLocator(this);
    }

    @Override
    protected void configure() {
        bind(jdbi).to(DBI.class);
        bind(jdbi.onDemand(StageDAO.class)).to(StageDAO.class);
        bind(jdbi.onDemand(WorkerDAO.class)).to(WorkerDAO.class);
        bind(jdbi.onDemand(TaskDAO.class)).to(TaskDAO.class);
        bind(jdbi.onDemand(AnswerDAO.class)).to(AnswerDAO.class);

        bindAsContract(StagesService.class).in(Singleton.class);
        bindAsContract(MetaResource.class).in(Singleton.class);
        bindAsContract(StageResource.class).in(Singleton.class);

        bindAsContract(MechanicalTsarVersion.class).in(Singleton.class);
        bindAsContract(MechanicalTsarVersionHealthCheck.class).in(Singleton.class);
    }

    public Map<String, Stage> getStages() {
        return locator.getService(StagesService.class).getStages();
    }
}
