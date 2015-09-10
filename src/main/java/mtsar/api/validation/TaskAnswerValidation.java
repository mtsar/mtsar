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
import mtsar.api.Task;
import org.inferred.freebuilder.FreeBuilder;

import java.util.ArrayList;
import java.util.List;

@FreeBuilder
public interface TaskAnswerValidation {
    Task getTask();

    Answer getAnswer();

    @ValidationMethod(message = "#answer-not-in-task: task has no such an answer in possible ones")
    default boolean isAnswerInDomain() {
        final List<String> answers = new ArrayList<>(getAnswer().getAnswers());
        answers.removeAll(getTask().getAnswers());
        return answers.isEmpty();
    }

    @ValidationMethod(message = "#task-single-no-answer: task type 'single' requires one answer")
    default boolean isAnswerPresentForTypeSingle() {
        return !getTask().getType().equalsIgnoreCase("single") || getAnswer().getAnswers().size() == 1;
    }

    class Builder extends TaskAnswerValidation_Builder {
    }
}
