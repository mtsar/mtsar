package mtsar.processors.answer;

import mtsar.api.Answer;
import mtsar.api.AnswerAggregation;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import mtsar.processors.AnswerAggregator;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;

public class RandomAggregator implements AnswerAggregator {
    protected final Provider<Process> process;
    protected final AnswerDAO answerDAO;

    @Inject
    public RandomAggregator(Provider<Process> processProvider, AnswerDAO answerDAO) {
        this.process = processProvider;
        this.answerDAO = answerDAO;
    }

    @Override
    public Map<Task, AnswerAggregation> aggregate(Collection<Task> tasks) {
        final Map<Task, AnswerAggregation> aggregations = new HashMap<>();
        for (final Task task : tasks) {
            final List<Answer> answers = answerDAO.listForTask(task.getId(), process.get().getId());
            if (answers.isEmpty()) continue;
            Collections.shuffle(answers);
            aggregations.put(task, new AnswerAggregation.Builder().setTask(task).addAllAnswers(answers.get(0).getAnswers()).build());
        }
        return aggregations;
    }
}
