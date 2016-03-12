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

import io.dropwizard.jersey.PATCH;
import mtsar.api.*;
import mtsar.api.csv.AnswerAggregationCSV;
import mtsar.api.csv.AnswerCSV;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.views.AnswersView;
import org.apache.commons.csv.CSVParser;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Path("/answers")
@Produces(mtsar.util.MediaType.APPLICATION_JSON)
public class AnswerResource {
    private final Stage stage;
    private final TaskDAO taskDAO;
    private final WorkerDAO workerDAO;
    private final AnswerDAO answerDAO;

    public AnswerResource(Stage stage, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        this.stage = stage;
        this.taskDAO = taskDAO;
        this.workerDAO = workerDAO;
        this.answerDAO = answerDAO;
    }

    @GET
    public List<Answer> getAnswers() {
        return answerDAO.listForStage(stage.getId());
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public AnswersView getAnswersView(@Context UriInfo uriInfo) {
        return new AnswersView(uriInfo, stage, answerDAO);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postAnswersCSV(@Context UriInfo uriInfo, @FormDataParam("file") InputStream stream) throws IOException {
        try (final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            try (final CSVParser csv = new CSVParser(reader, AnswerCSV.FORMAT)) {
                answerDAO.insert(AnswerCSV.parse(stage, csv));
            }
        }
        answerDAO.resetSequence();
        return Response.seeOther(getAnswersURI(uriInfo)).build();
    }

    @GET
    @Path("aggregations")
    public Map<Integer, AnswerAggregation> getAnswerAggregations() {
        final List<Task> tasks = taskDAO.listForStage(stage.getId());
        final Map<Integer, AnswerAggregation> aggregations = stage.getAnswerAggregator().aggregate(tasks);
        return aggregations;
    }

    @GET
    @Path("aggregations.csv")
    @Produces(mtsar.util.MediaType.TEXT_CSV)
    public StreamingOutput getAnswerAggregationsCSV() {
        final Map<Integer, AnswerAggregation> aggregations = getAnswerAggregations();
        return output -> AnswerAggregationCSV.write(aggregations.values(), output);
    }

    @GET
    @Path("agreement")
    public AgreementReport getAgreementReport() {
        return new AgreementReport.Builder().compute(stage, answerDAO).build();
    }

    @GET
    @Path("{answer}")
    public Answer getAnswer(@PathParam("answer") Integer id) {
        return fetchAnswer(id);
    }

    @PATCH
    @Path("{answer}")
    public Answer patchAnswer(@PathParam("answer") Integer id) {
        final Answer answer = fetchAnswer(id);
        throw new WebApplicationException(Response.Status.NOT_IMPLEMENTED);
    }

    @DELETE
    @Path("{answer}")
    public Answer deleteAnswer(@PathParam("answer") Integer id) {
        final Answer answer = fetchAnswer(id);
        answerDAO.delete(id, stage.getId());
        return answer;
    }

    @DELETE
    public void deleteAnswers() {
        answerDAO.deleteAll(stage.getId());
        answerDAO.resetSequence();
    }

    private Answer fetchAnswer(Integer id) {
        final Answer answer = answerDAO.find(id, stage.getId());
        if (answer == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
        return answer;
    }

    private URI getAnswersURI(UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().
                path("processes").path(stage.getId()).
                path("answers").
                build();
    }
}
