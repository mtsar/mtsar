package mtsar.worker;

import mtsar.api.ProcessDefinition;
import mtsar.api.Worker;
import mtsar.processors.WorkerRanker;
import mtsar.processors.worker.ZeroRanker;
import org.junit.Before;
import org.junit.Test;

import static mtsar.TestHelper.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZeroRankerTest {
    private static final ProcessDefinition process = mock(ProcessDefinition.class);
    private static final Worker worker = fixture("worker1.json", Worker.class);
    private static final WorkerRanker ranker = new ZeroRanker();

    @Before
    public void setup() {
        when(process.getId()).thenReturn("1");
    }

    @Test
    public void testRanking() {
        assertThat(ranker.rank(worker).isPresent()).isTrue();
        assertThat(ranker.rank(worker).get().getReputation()).isEqualTo(0);
    }
}
