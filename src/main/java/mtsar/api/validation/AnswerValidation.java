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
