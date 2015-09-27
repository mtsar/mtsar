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

package mtsar.answer;

import mtsar.api.AnswerAggregation;
import mtsar.api.Task;
import org.junit.Test;

import static mtsar.TestHelper.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class AnswerAggregationTest {
    private static final Task task = fixture("task1.json", Task.class);

    @Test
    public void testDefault() {
        final AnswerAggregation aggregation = new AnswerAggregation.Builder().setTask(task).build();
        assertThat(aggregation.getType()).isEqualTo(AnswerAggregation.TYPE_DEFAULT);
        assertThat(aggregation.getTask()).isEqualTo(task);
        assertThat(aggregation.getAnswers()).isEmpty();
    }

    @Test
    public void testEmpty() {
        final AnswerAggregation aggregation = AnswerAggregation.empty(task);
        assertThat(aggregation.getType()).isEqualTo(AnswerAggregation.TYPE_EMPTY);
        assertThat(aggregation.getTask()).isEqualTo(task);
        assertThat(aggregation.getAnswers()).isEmpty();
    }
}
