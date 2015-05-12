package mtsar.processors.answer;

import mtsar.api.Answer;
import mtsar.api.Task;
import mtsar.api.jdbi.AnswerDAO;
import mtsar.api.jdbi.TaskDAO;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.Processor;

import javax.inject.Inject;
import java.util.*;

public class RandomAggregator extends Processor implements AnswerAggregator {
    protected final AnswerDAO answerDAO;

    @Inject
    public RandomAggregator(AnswerDAO answerDAO) {
        this.answerDAO = answerDAO;
    }

    @Override
    public Optional<Answer> aggregate(Task task) {
        final List<Answer> answers = answerDAO.listForTask(task.getId(), process.getId());
        if (answers.isEmpty()) return Optional.empty();
        Collections.shuffle(answers);
        return Optional.ofNullable(answers.get(0));
    }
}
