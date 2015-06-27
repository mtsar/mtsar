package mtsar.processors.answer;

import mtsar.api.Answer;
import mtsar.api.AnswerAggregation;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.jdbi.AnswerDAO;
import mtsar.processors.AnswerAggregator;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RandomAggregator implements AnswerAggregator {
    protected final Provider<Process> process;
    protected final AnswerDAO answerDAO;

    @Inject
    public RandomAggregator(Provider<Process> processProvider, AnswerDAO answerDAO) {
        this.process = processProvider;
        this.answerDAO = answerDAO;
    }

    @Override
    public Optional<AnswerAggregation> aggregate(Task task) {
        final List<Answer> answers = answerDAO.listForTask(task.getId(), process.get().getId());
        if (answers.isEmpty()) return Optional.empty();
        Collections.shuffle(answers);
        return Optional.of(AnswerAggregation.create(task, Answer.builder().setAnswer(answers.get(0).getAnswer()).build()));
    }
}
