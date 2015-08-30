package mtsar.processors.task;

import mtsar.api.*;
import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.TaskAllocator;
import org.apache.commons.lang3.tuple.Pair;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import javax.annotation.Nonnegative;
import javax.inject.Inject;
import javax.inject.Provider;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class InverseCountAllocator implements TaskAllocator {
    protected final Provider<Process> process;
    protected final DBI dbi;
    protected final TaskDAO taskDAO;
    protected final AnswerDAO answerDAO;
    protected final CountDAO countDAO;

    @Inject
    public InverseCountAllocator(Provider<Process> processProvider, DBI dbi, TaskDAO taskDAO, AnswerDAO answerDAO) {
        this.process = processProvider;
        this.dbi = dbi;
        this.taskDAO = taskDAO;
        this.answerDAO = answerDAO;
        this.countDAO = dbi.onDemand(CountDAO.class);
    }

    @Override
    public List<TaskAllocation> allocate(Worker worker, @Nonnegative int n) {
        final Set<Integer> answered = answerDAO.listForWorker(worker.getId(), process.get().getId()).stream().
                map(Answer::getTaskId).collect(Collectors.toSet());

        final Map<Integer, Integer> counts = countDAO.getCountsSQL(process.get().getId()).stream().
                filter(pair -> !answered.contains(pair.getKey())).
                collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        final List<Integer> ids = filterTasks(counts);
        final int taskRemaining = ids.size();

        if (ids.isEmpty()) return Collections.emptyList();
        if (taskRemaining > n) ids.subList(n, ids.size()).clear();
        final List<Task> tasks = taskDAO.select(ids, process.get().getId());

        final int taskCount = taskDAO.count(process.get().getId());
        return tasks.stream().map(task -> new TaskAllocation(worker, task, taskRemaining, taskCount)).collect(Collectors.toList());
    }

    protected List<Integer> filterTasks(Map<Integer, Integer> counts) {
        final List<Integer> ids = new ArrayList<>(counts.keySet());
        Collections.shuffle(ids);
        ids.sort((id1, id2) -> counts.get(id1).compareTo(counts.get(id2)));
        return ids;
    }

    @RegisterMapper(CountPairMapper.class)
    public interface CountDAO {
        @SqlQuery("select tasks.id, count(answers.id) from tasks left join answers on answers.task_id = tasks.id and answers.process = tasks.process and answers.type <> 'skip' where tasks.process = :process group by tasks.id")
        List<Pair<Integer, Integer>> getCountsSQL(@Bind("process") String process);
    }

    public static class CountPairMapper implements ResultSetMapper<Pair> {
        public Pair<Integer, Integer> map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return Pair.of(r.getInt("id"), r.getInt("count"));
        }
    }
}
