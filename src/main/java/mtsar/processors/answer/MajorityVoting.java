package mtsar.processors.answer;

import mtsar.api.Answer;
import mtsar.api.AnswerAggregation;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.jdbi.AnswerDAO;
import mtsar.processors.AnswerAggregator;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MajorityVoting implements AnswerAggregator {
    public static final Comparator<Map.Entry<String, Long>> voteComparator =
            (e1, e2) -> Long.compare(e2.getValue(), e1.getValue());

    protected final Provider<Process> process;
    protected final AnswerDAO answerDAO;

    @Inject
    public MajorityVoting(Provider<Process> processProvider, AnswerDAO answerDAO) {
        this.process = processProvider;
        this.answerDAO = answerDAO;
    }

    @Override
    public Optional<AnswerAggregation> aggregate(Task task) {
        final List<Answer> answers = answerDAO.listForTask(task.getId(), process.get().getId());
        final Map<String, Long> votes = answers.stream().collect(
                Collectors.groupingBy(Answer::getAnswer, Collectors.counting()));
        final Optional<Map.Entry<String, Long>> winner =
                votes.entrySet().stream().sorted(voteComparator).findFirst();
        if (!winner.isPresent()) return Optional.empty();
        final Optional<Answer> answer = answers.stream().filter(a -> a.getAnswer() == winner.get().getKey()).findFirst();
        if (!answer.isPresent()) return Optional.empty();
        return Optional.of(AnswerAggregation.create(task, Answer.builder().setAnswer(answer.get().getAnswer()).build()));
    }
}
