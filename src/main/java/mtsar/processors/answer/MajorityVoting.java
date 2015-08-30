package mtsar.processors.answer;

import mtsar.api.AnswerAggregation;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import mtsar.processors.AnswerAggregator;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class MajorityVoting implements AnswerAggregator {
    protected final Provider<Process> process;
    protected final AnswerDAO answerDAO;

    @Inject
    public MajorityVoting(Provider<Process> processProvider, AnswerDAO answerDAO) {
        this.process = processProvider;
        this.answerDAO = answerDAO;
    }

    @Override
    public Map<Task, AnswerAggregation> aggregate(Collection<Task> tasks) {
        throw new UnsupportedOperationException("Not Implemented Yet");
    }
}
