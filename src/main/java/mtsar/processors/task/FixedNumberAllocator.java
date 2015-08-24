package mtsar.processors.task;

import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import org.skife.jdbi.v2.DBI;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class FixedNumberAllocator extends InverseCountAllocator {
    private Integer answersPerTask = null;

    @Inject
    public FixedNumberAllocator(Provider<Process> processProvider, DBI dbi, TaskDAO taskDAO, AnswerDAO answerDAO) {
        super(processProvider, dbi, taskDAO, answerDAO);
    }

    @Override
    protected List<Integer> filterTasks(Map<Integer, Integer> counts) {
        checkAnswersPerTask();
        final List<Integer> ids = counts.entrySet().stream().
                filter(entry -> entry.getValue() < answersPerTask).
                map(Map.Entry::getKey).collect(Collectors.toList());
        Collections.shuffle(ids);
        ids.sort((id1, id2) -> counts.get(id1).compareTo(counts.get(id2)));
        return ids;
    }

    private void checkAnswersPerTask() {
        if (this.answersPerTask != null) return;
        this.answersPerTask = checkNotNull(Integer.parseInt(process.get().getOptions().get("answersPerTask")), "answersPerTask option is not set");
    }
}
