package mtsar.worker;

import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.processors.worker.RandomRanker;
import mtsar.processors.worker.ZeroRanker;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RandomRankerTest {
    private static final Process process = mock(Process.class);
    private static final Worker worker = mock(Worker.class);
    private static final Task task = mock(Task.class);
    private static final RandomRanker randomRanker = new RandomRanker();

    @Before
    public void setup() {
        when(process.getId()).thenReturn("1");
        randomRanker.setProcess(process);
    }

    @Test
    public void testRanking() {
        assertThat(randomRanker.estimatePerformance(worker)).isBetween(0.0, 1.0);
        assertThat(randomRanker.estimatePerformance(worker, task)).isBetween(0.0, 1.0);
    }
}
