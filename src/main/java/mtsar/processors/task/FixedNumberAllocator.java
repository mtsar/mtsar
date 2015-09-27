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

package mtsar.processors.task;

import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import org.apache.commons.lang3.tuple.Triple;
import org.skife.jdbi.v2.DBI;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class FixedNumberAllocator extends InverseCountAllocator {
    private Integer answersPerTask = null;

    @Inject
    public FixedNumberAllocator(Provider<Process> processProvider, DBI dbi, TaskDAO taskDAO, AnswerDAO answerDAO) {
        super(processProvider, dbi, taskDAO, answerDAO);
    }

    @Override
    protected List<Integer> filterTasks(Map<Integer, Integer> counts) {
        checkAnswersPerTask();
        return counts.entrySet().stream().
                filter(entry -> entry.getValue() < answersPerTask).
                map(entry -> Triple.of(entry.getKey(), entry.getValue(), Math.random())).
                sorted(INVERSE_COUNT).
                map(Triple::getLeft).
                collect(Collectors.toList());
    }

    private void checkAnswersPerTask() {
        if (this.answersPerTask != null) return;
        this.answersPerTask = requireNonNull(Integer.parseInt(process.get().getOptions().get("answersPerTask")), "answersPerTask option is not set");
    }
}
