package mtsar.processors.answer;

import mtsar.api.AnswerAggregation;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import mtsar.processors.AnswerAggregator;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

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
        throw new UnsupportedOperationException("Not Implemented Yet");
    }
}
