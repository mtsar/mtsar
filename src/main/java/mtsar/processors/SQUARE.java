package mtsar.processors;

import mtsar.api.Answer;
import mtsar.api.Stage;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import org.square.qa.utilities.constructs.Models;
import org.square.qa.utilities.constructs.workersDataStruct;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SQUARE (Statistical QUality Assurance Robustness Evaluation) is a benchmark for comparative evaluation of
 * consensus methods for human computation / crowdsourcing.
 *
 * This class bridges the gap between Mechanical Tsar and the SQUARE algorithms.
 *
 * @see <a href="http://www.aaai.org/ocs/index.php/HCOMP/HCOMP13/paper/view/7550">HCOMP13/7550</a>
 */
public abstract class SQUARE {
    protected Models<Integer, Integer, String> compute(Stage stage, AnswerDAO answerDAO, Map<Integer, Task> taskMap) {
        final Models<Integer, Integer, String> models = new Models<>();

        final Set<String> categories = taskMap.values().stream().flatMap(t -> t.getAnswers().stream()).collect(Collectors.toSet());
        models.setResponseCategories(new TreeSet<>(categories));

        final Map<Integer, workersDataStruct<Integer, String>> workers = new HashMap<>();
        final List<Answer> answers = answerDAO.listForStage(stage.getId());
        for (final Answer answer : answers) {
            if (!answer.getType().equalsIgnoreCase(AnswerDAO.ANSWER_TYPE_ANSWER)) continue;
            if (answer.getAnswers().isEmpty()) continue;
            if (!workers.containsKey(answer.getWorkerId()))
                workers.put(answer.getWorkerId(), new workersDataStruct<>());
            final workersDataStruct<Integer, String> datum = workers.get(answer.getWorkerId());
            datum.insertWorkerResponse(answer.getTaskId(), answer.getAnswer().get());
        }
        models.setWorkersMap(workers);

        return models;
    }
}
