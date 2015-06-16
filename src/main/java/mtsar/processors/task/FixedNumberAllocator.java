package mtsar.processors.task;

import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.api.jdbi.AnswerDAO;
import mtsar.api.jdbi.TaskDAO;
import mtsar.processors.TaskAllocator;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Optional;

public class FixedNumberAllocator implements TaskAllocator {
    protected final Provider<Process> process;
    protected final TaskDAO taskDAO;
    protected final AnswerDAO answerDAO;
    protected int answersPerTask;

    @Inject
    public FixedNumberAllocator(Provider<Process> processProvider, TaskDAO taskDAO, AnswerDAO answerDAO) {
        this.process = processProvider;
        this.taskDAO = taskDAO;
        this.answerDAO = answerDAO;
    }

    @Override
    public Optional<Task> allocate(Worker w) {
        ensureAnswersPerTask();
        throw new UnsupportedOperationException("Not Implemented Yet");
    }

    protected void ensureAnswersPerTask() {
        final Integer answersPerTask = Integer.valueOf(process.get().getOptions().get("answersPerTask"));
        if (answersPerTask == null) {
            throw new RuntimeException("answersPerTask option is not set");
        }
        this.answersPerTask = answersPerTask;
    }
}
