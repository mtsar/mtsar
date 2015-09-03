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

package mtsar.dropwizard.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import mtsar.api.sql.*;
import org.skife.jdbi.v2.DBI;

import javax.inject.Singleton;
import java.util.logging.Logger;

public class DatabaseModule extends AbstractModule {
    private final DBI jdbi;

    public DatabaseModule(DBI jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    protected void configure() {
        bind(DBI.class).toInstance(jdbi);
    }

    @Provides
    @Singleton
    public ProcessDAO provideProcessDAO(DBI jdbi, Logger logger) {
        logger.info(String.format("Providing a ProcessDAO"));
        return jdbi.onDemand(ProcessDAO.class);
    }

    @Provides
    @Singleton
    public WorkerDAO provideWorkerDAO(DBI jdbi, Logger logger) {
        logger.info(String.format("Providing a WorkerDAO"));
        return jdbi.onDemand(WorkerDAO.class);
    }

    @Provides
    @Singleton
    public TaskDAO provideTaskDAO(DBI jdbi, Logger logger) {
        logger.info(String.format("Providing a TaskDAO"));
        return jdbi.onDemand(TaskDAO.class);
    }

    @Provides
    @Singleton
    public AnswerDAO provideAnswerDAO(DBI jdbi, Logger logger) {
        logger.info(String.format("Providing an AnswerDAO"));
        return jdbi.onDemand(AnswerDAO.class);
    }

    @Provides
    @Singleton
    public EventDAO provideEventDAO(DBI jdbi, Logger logger) {
        logger.info(String.format("Providing an EventDAO"));
        return jdbi.onDemand(EventDAO.class);
    }
}
