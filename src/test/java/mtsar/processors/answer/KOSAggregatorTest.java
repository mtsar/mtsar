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

package mtsar.processors.answer;

import mtsar.api.Answer;
import mtsar.api.AnswerAggregation;
import mtsar.api.Stage;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.AnswerAggregator;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static mtsar.TestHelper.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class KOSAggregatorTest {
    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final AnswerDAO answerDAO = mock(AnswerDAO.class);
    private static final Stage stage = mock(Stage.class);
    private static final Task task1 = fixture("task1.json", Task.class);
    private static final Task task2 = fixture("task2.json", Task.class);
    private static final AnswerAggregator aggregator = new KOSAggregator(stage, taskDAO, answerDAO);

    @Before
    public void setup() {
        reset(taskDAO);
        reset(answerDAO);
        when(stage.getId()).thenReturn("1");
    }

    @Test
    public void testTwoTasks() {
        when(taskDAO.listForStage(anyString())).thenReturn(Arrays.asList(task1, task2));
        when(answerDAO.listForStage(anyString())).thenReturn(Arrays.asList(
                new Answer.Builder().setWorkerId(1).setTaskId(1).addAnswers("1").buildPartial(),
                new Answer.Builder().setWorkerId(2).setTaskId(1).addAnswers("1").buildPartial(),
                new Answer.Builder().setWorkerId(3).setTaskId(1).addAnswers("1").buildPartial(),
                new Answer.Builder().setWorkerId(1).setTaskId(2).addAnswers("2").buildPartial(),
                new Answer.Builder().setWorkerId(2).setTaskId(2).addAnswers("2").buildPartial(),
                new Answer.Builder().setWorkerId(3).setTaskId(2).addAnswers("2").buildPartial()
        ));
        {
            final Optional<AnswerAggregation> winner = aggregator.aggregate(task1);
            assertThat(winner.isPresent()).isTrue();
            assertThat(winner.get().getAnswers()).hasSize(1);
            assertThat(winner.get().getAnswers().get(0)).isEqualTo("1");
        }
        {
            final Optional<AnswerAggregation> winner = aggregator.aggregate(task2);
            assertThat(winner.isPresent()).isTrue();
            assertThat(winner.get().getAnswers()).hasSize(1);
            assertThat(winner.get().getAnswers().get(0)).isEqualTo("2");
        }
    }

    @Test
    public void testEmptyCase() {
        when(answerDAO.listForStage(anyString())).thenReturn(Collections.emptyList());
        final Optional<AnswerAggregation> winner = aggregator.aggregate(task1);
        assertThat(winner.isPresent()).isFalse();
    }
}
