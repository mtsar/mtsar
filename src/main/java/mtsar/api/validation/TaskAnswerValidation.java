package mtsar.api.validation;

import io.dropwizard.validation.ValidationMethod;
import mtsar.api.Answer;
import mtsar.api.Task;

public class TaskAnswerValidation {
    private final Task task;
    private final Answer answer;

    public TaskAnswerValidation(Task task, Answer answer) {
        this.task = task;
        this.answer = answer;
    }

    @ValidationMethod(message = "#task-single-no-answer: task type 'single' requires one answer")
    public boolean isAnswerPresentForTypeSingle() {
        return !task.getType().equalsIgnoreCase("single") || answer.getAnswers().size() == 1;
    }
}
