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

package mtsar.views;

import io.dropwizard.views.View;
import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import static com.google.common.base.Preconditions.checkNotNull;

public class AnswersView extends View {
    private final UriInfo uriInfo;
    private final Process process;
    private final AnswerDAO answerDAO;

    @Inject
    public AnswersView(UriInfo uriInfo, Process process, AnswerDAO answerDAO) {
        super("answers.mustache");
        checkNotNull(this.uriInfo = uriInfo);
        checkNotNull(this.process = process);
        checkNotNull(this.answerDAO = answerDAO);
    }

    @SuppressWarnings("unused")
    public String getTitle() {
        return String.format("Answers of \"%s\"", process.getId());
    }

    @SuppressWarnings("unused")
    public Process getProcess() {
        return process;
    }

    @SuppressWarnings("unused")
    public int getAnswerCount() {
        return answerDAO.count(process.getId());
    }

    @SuppressWarnings("unused")
    public String getProcessPath() {
        return uriInfo.getBaseUriBuilder().
                path("processes").
                path(process.getId()).
                toString();
    }

    @SuppressWarnings("unused")
    public String getPath() {
        return uriInfo.getBaseUriBuilder().
                path("processes").
                path(process.getId()).
                path("answers").
                toString();
    }
}
