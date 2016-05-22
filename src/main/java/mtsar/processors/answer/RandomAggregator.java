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
import mtsar.processors.AnswerAggregator;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class RandomAggregator implements AnswerAggregator {
    @Inject
    protected Stage stage;
    protected final AnswerDAO answerDAO;

    @Inject
    protected RandomAggregator(AnswerDAO answerDAO) {
        this.answerDAO = requireNonNull(answerDAO);
    }

    public RandomAggregator(Stage stage, AnswerDAO answerDAO) {
        this(answerDAO);
        this.stage = requireNonNull(stage);
    }

    @Override
    @Nonnull
    public Map<Integer, AnswerAggregation> aggregate(@Nonnull Collection<Task> tasks) {
        requireNonNull(stage, "the stage provider should not provide null");
        final Map<Integer, AnswerAggregation> aggregations = new HashMap<>();
        for (final Task task : tasks) {
            final List<Answer> answers = answerDAO.listForTask(task.getId(), stage.getId());
            if (answers.isEmpty()) continue;
            Collections.shuffle(answers);
            aggregations.put(task.getId(), new AnswerAggregation.Builder().setTask(task).addAllAnswers(answers.get(0).getAnswers()).build());
        }
        return aggregations;
    }
}
