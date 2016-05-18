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

package mtsar.resources;

import mtsar.MechanicalTsarVersion;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.dropwizard.hk2.StagesService;
import mtsar.views.DashboardView;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("/")
@Produces(mtsar.util.MediaType.APPLICATION_JSON)
public class MetaResource {
    private final MechanicalTsarVersion version;
    private final TaskDAO taskDAO;
    private final WorkerDAO workerDAO;
    private final AnswerDAO answerDAO;
    private final StagesService stagesService;

    @Inject
    public MetaResource(StagesService stagesService, MechanicalTsarVersion version, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        this.stagesService = stagesService;
        this.version = version;
        this.taskDAO = taskDAO;
        this.workerDAO = workerDAO;
        this.answerDAO = answerDAO;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public DashboardView getDashboardView() {
        return new DashboardView(version, stagesService.getStages(), taskDAO, workerDAO, answerDAO);
    }

    @GET
    @Path("version")
    @Produces({MediaType.TEXT_PLAIN, mtsar.util.MediaType.APPLICATION_JSON})
    public String getVersion() {
        return version.getVersion();
    }
}
