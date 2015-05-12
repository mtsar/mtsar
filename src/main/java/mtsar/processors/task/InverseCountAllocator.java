package mtsar.processors.task;

import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.api.jdbi.AnswerDAO;
import mtsar.api.jdbi.TaskDAO;
import mtsar.processors.Processor;
import mtsar.processors.TaskAllocator;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class InverseCountAllocator extends Processor implements TaskAllocator {
    private final TaskDAO taskDAO;
    private final AnswerDAO answerDAO;

    @Inject
    public InverseCountAllocator(TaskDAO taskDAO, AnswerDAO answerDAO) {
        this.taskDAO = taskDAO;
        this.answerDAO = answerDAO;
    }

    @Override
    public Optional<Task> allocate(Worker w) {
        final List<Task> tasks = taskDAO.listForProcess(process.getId());
        return tasks.stream().sorted((t1, t2) -> Integer.compare(
                answerDAO.listForTask(t2.getId(), process.getId()).size(),
                answerDAO.listForTask(t1.getId(), process.getId()).size()
        )).findFirst();
    }
}
