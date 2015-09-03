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

package mtsar.api.validation;

import io.dropwizard.validation.ValidationMethod;
import mtsar.api.Answer;
import mtsar.api.sql.AnswerDAO;

public class AnswerValidation {
    private final Answer answer;
    private final AnswerDAO answerDAO;

    public AnswerValidation(Answer answer, AnswerDAO answerDAO) {
        this.answer = answer;
        this.answerDAO = answerDAO;
    }

    @ValidationMethod(message = "#answer-duplicate: worker has already completed this task")
    public boolean isAnswerUnique() {
        return answerDAO.findByWorkerAndTask(answer.getProcess(), answer.getWorkerId(), answer.getTaskId()) == null;
    }
}
