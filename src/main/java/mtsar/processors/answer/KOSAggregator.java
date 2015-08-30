package mtsar.processors.answer;

import mtsar.api.AnswerAggregation;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.AnswerAggregator;

import javax.inject.Provider;
import java.util.Collection;
import java.util.Map;

/**
 * Implementation of the answer aggregation algorithm proposed by Karger, Oh & Shah for binary tasks.
 *
 * @see <a href="http://pubsonline.informs.org/doi/abs/10.1287/opre.2013.1235">10.1287/opre.2013.1235</a>
 * @see MajorityVoting
 */
public class KOSAggregator implements AnswerAggregator {
    private final Provider<Process> process;
    private final TaskDAO taskDAO;
    private final AnswerDAO answerDAO;

    public KOSAggregator(Provider<Process> process, TaskDAO taskDAO, AnswerDAO answerDAO) {
        this.process = process;
        this.taskDAO = taskDAO;
        this.answerDAO = answerDAO;
    }

    @Override
    public Map<Task, AnswerAggregation> aggregate(Collection<Task> tasks) {
        throw new UnsupportedOperationException("Not Implemented Yet");
    }
}
