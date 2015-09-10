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
import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.sql.AnswerDAO;
import mtsar.processors.AnswerAggregator;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;

public class RandomAggregator implements AnswerAggregator {
    protected final Provider<Process> process;
    protected final AnswerDAO answerDAO;

    @Inject
    public RandomAggregator(Provider<Process> processProvider, AnswerDAO answerDAO) {
        this.process = processProvider;
        this.answerDAO = answerDAO;
    }

    @Override
    @Nonnull
    public Map<Task, AnswerAggregation> aggregate(@Nonnull Collection<Task> tasks) {
        final Map<Task, AnswerAggregation> aggregations = new HashMap<>();
        for (final Task task : tasks) {
            final List<Answer> answers = answerDAO.listForTask(task.getId(), process.get().getId());
            if (answers.isEmpty()) continue;
            Collections.shuffle(answers);
            aggregations.put(task, new AnswerAggregation.Builder().setTask(task).addAllAnswers(answers.get(0).getAnswers()).build());
        }
        return aggregations;
    }
}
