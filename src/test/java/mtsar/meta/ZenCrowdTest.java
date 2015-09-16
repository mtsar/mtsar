/*
 * Copyright 2015 Dmitry Ustalov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mtsar.meta;

import com.google.common.collect.Lists;
import mtsar.api.*;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.meta.ZenCrowd;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static mtsar.TestHelper.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ZenCrowdTest {
    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final AnswerDAO answerDAO = mock(AnswerDAO.class);
    private static final mtsar.api.Process process = mock(mtsar.api.Process.class);
    private static final Task task1 = fixture("task1.json", Task.class);
    private static final Task task2 = fixture("task2.json", Task.class);
    private static final Worker worker1 = fixture("worker1.json", Worker.class);
    private static final Worker worker2 = fixture("worker2.json", Worker.class);
    private static final ZenCrowd processor = new ZenCrowd(() -> process, taskDAO, answerDAO);

    @Before
    public void setup() {
        reset(taskDAO);
        reset(answerDAO);
        when(process.getId()).thenReturn("1");
        when(taskDAO.listForProcess(anyString())).thenReturn(Lists.newArrayList(task1, task2));
        when(answerDAO.listForProcess(anyString())).thenReturn(Lists.newArrayList(
                new Answer.Builder().setWorkerId(1).setTaskId(1).addAnswers("1").buildPartial(),
                new Answer.Builder().setWorkerId(2).setTaskId(1).addAnswers("1").buildPartial(),
                new Answer.Builder().setWorkerId(1).setTaskId(2).addAnswers("1").buildPartial(),
                new Answer.Builder().setWorkerId(2).setTaskId(2).addAnswers("1").buildPartial()
        ));
    }

    @Test
    public void testTwoTasks() {
        final Optional<AnswerAggregation> aggregation = processor.aggregate(task1);
        assertThat(aggregation.isPresent()).isTrue();
        assertThat(aggregation.get().getAnswers()).hasSize(1);
        assertThat(aggregation.get().getAnswers().get(0)).isEqualTo("1");

        final Map<Task, AnswerAggregation> aggregations = processor.aggregate(Lists.newArrayList(task1, task2));
        assertThat(aggregations).hasSize(2);
        assertThat(aggregations.get(task1).getAnswers()).hasSize(1);
        assertThat(aggregations.get(task1).getAnswers()).contains("1");
        assertThat(aggregations.get(task2).getAnswers()).hasSize(1);
        assertThat(aggregations.get(task2).getAnswers()).contains("1");
    }

    @Test
    public void testTwoWorkers() {
        final Optional<WorkerRanking> ranking = processor.rank(worker1);
        assertThat(ranking.isPresent()).isTrue();
        assertThat(ranking.get().getReputation()).isEqualTo(1.0);

        final Map<Worker, WorkerRanking> rankings = processor.rank(Lists.newArrayList(worker1, worker2));
        assertThat(rankings).hasSize(2);
        assertThat(rankings.get(worker1).getReputation()).isEqualTo(1.0);
        assertThat(rankings.get(worker2).getReputation()).isEqualTo(1.0);
    }

    @Test
    public void testEmptyCase() {
        reset(answerDAO);
        when(answerDAO.listForProcess(anyString())).thenReturn(Collections.emptyList());
        final Optional<AnswerAggregation> winner = processor.aggregate(task1);
        assertThat(winner.isPresent()).isFalse();
    }
}
