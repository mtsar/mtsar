package mtsar.processors.answer;

import mtsar.api.Answer;
import mtsar.api.Task;
import mtsar.api.jdbi.AnswerDAO;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.Processor;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MajorityVoting extends Processor implements AnswerAggregator {
    public static final Comparator<Map.Entry<String, Long>> voteComparator =
            (e1, e2) -> Long.compare(e2.getValue(), e1.getValue());

    protected final AnswerDAO answerDAO;

    @Inject
    public MajorityVoting(AnswerDAO answerDAO) {
        this.answerDAO = answerDAO;
    }

    @Override
    public Optional<Answer> aggregateAnswers(Task task) {
        final List<Answer> answers = answerDAO.listForTask(task.getId(), getProcess().getId());
        final Map<String, Long> votes = answers.stream().collect(
                Collectors.groupingBy(Answer::getAnswer, Collectors.counting()));
        final Optional<Map.Entry<String, Long>> winner =
                votes.entrySet().stream().sorted(voteComparator).findFirst();
        if (!winner.isPresent()) return Optional.empty();
        return answers.stream().filter(answer -> answer.getAnswer() == winner.get().getKey()).findFirst();
    }
}
