package mtsar.processors.task;

import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class FixedNumberAllocator extends InverseCountAllocator {
    private Integer answersPerTask = null;

    @Inject
    public FixedNumberAllocator(Provider<Process> processProvider, TaskDAO taskDAO, AnswerDAO answerDAO) {
        super(processProvider, taskDAO, answerDAO);
    }

    @Override
    protected Map<Task, Integer> getCounts(List<Task> tasks, Worker worker) {
        ensureAnswersPerTask();

        final Map<Task, Boolean> answers = tasks.parallelStream().collect(
                Collectors.toMap(Function.identity(), t -> answerDAO.listForTask(t.getId(), process.get().getId()).stream().anyMatch(a -> a.getWorkerId().equals(worker.getId())))
        );

        final Map<Task, Integer> counts = super.getCounts(tasks, worker);

        final Iterator<Map.Entry<Task, Integer>> iterator = counts.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Task, Integer> entry = iterator.next();
            if (answers.get(entry.getKey()) || (entry.getValue() >= answersPerTask)) iterator.remove();
        }

        return counts;
    }

    private void ensureAnswersPerTask() {
        final Integer answersPerTask = Integer.valueOf(process.get().getOptions().get("answersPerTask"));
        this.answersPerTask = checkNotNull(answersPerTask, "answersPerTask option is not set");
    }
}
