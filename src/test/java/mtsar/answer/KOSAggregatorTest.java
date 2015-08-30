package mtsar.answer;

import mtsar.api.AnswerAggregation;
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.answer.KOSAggregator;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class KOSAggregatorTest {
    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final AnswerDAO answerDAO = mock(AnswerDAO.class);
    private static final mtsar.api.Process process = mock(Process.class);
    private static final Task task = mock(Task.class);
    private static final KOSAggregator aggregator = new KOSAggregator(() -> process, taskDAO, answerDAO);

    @Before
    public void setup() {
        reset(taskDAO);
        reset(answerDAO);
        when(process.getId()).thenReturn("1");
        when(task.getId()).thenReturn(1);
    }

    @Test
    public void testEmptyCase() {
        when(answerDAO.listForTask(any(), anyString())).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> {
            final Optional<AnswerAggregation> winner = aggregator.aggregate(task);
            assertThat(winner.isPresent()).isFalse();
        }).isInstanceOf(UnsupportedOperationException.class).hasMessageContaining("Not Implemented Yet");
    }
}
