package mtsar.worker;

import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.processors.worker.ZeroRanker;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZeroRankerTest {
    private static final Process process = mock(Process.class);
    private static final Worker worker = mock(Worker.class);
    private static final Task task = mock(Task.class);
    private static final ZeroRanker zeroRanker = new ZeroRanker();

    @Before
    public void setup() {
        when(process.getId()).thenReturn("1");
        zeroRanker.setProcess(process);
    }

    @Test
    public void testRanking() {
        assertThat(zeroRanker.rank(worker)).isEqualTo(0);
        assertThat(zeroRanker.rank(worker, task)).isEqualTo(0);
    }
}
