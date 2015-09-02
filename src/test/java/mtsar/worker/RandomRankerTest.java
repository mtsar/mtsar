package mtsar.worker;

import mtsar.api.ProcessDefinition;
import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.processors.worker.RandomRanker;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RandomRankerTest {
    private static final ProcessDefinition process = mock(ProcessDefinition.class);
    private static final Worker worker = new Worker.Builder().setId(1).setProcess("1").build();
    private static final Task task = mock(Task.class);
    private static final RandomRanker ranker = new RandomRanker();

    @Before
    public void setup() {
        when(process.getId()).thenReturn("1");
    }

    @Test
    public void testRanking() {
        assertThat(ranker.rank(worker).isPresent()).isTrue();
        assertThat(ranker.rank(worker).get().getReputation()).isBetween(0.0, 1.0);

        assertThat(ranker.rank(worker, task).isPresent()).isTrue();
        assertThat(ranker.rank(worker, task).get().getReputation()).isBetween(0.0, 1.0);
    }
}
