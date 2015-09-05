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

import java.util.ArrayList;
import java.util.List;

public class TaskAnswerValidation {
    private final Task task;
    private final Answer answer;

    public TaskAnswerValidation(Task task, Answer answer) {
        this.task = task;
        this.answer = answer;
    }

    @ValidationMethod(message = "#answer-not-in-task: task has no such an answer in possible ones")
    public boolean isAnswerInDomain() {
        final List<String> answers = new ArrayList<>(answer.getAnswers());
        answers.removeAll(task.getAnswers());
        return answers.isEmpty();
    }

    @ValidationMethod(message = "#task-single-no-answer: task type 'single' requires one answer")
    public boolean isAnswerPresentForTypeSingle() {
        return !task.getType().equalsIgnoreCase("single") || answer.getAnswers().size() == 1;
    }
}
