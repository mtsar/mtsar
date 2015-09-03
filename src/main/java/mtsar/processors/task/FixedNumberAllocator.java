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
import org.skife.jdbi.v2.DBI;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class FixedNumberAllocator extends InverseCountAllocator {
    private Integer answersPerTask = null;

    @Inject
    public FixedNumberAllocator(Provider<Process> processProvider, DBI dbi, TaskDAO taskDAO, AnswerDAO answerDAO) {
        super(processProvider, dbi, taskDAO, answerDAO);
    }

    @Override
    protected List<Integer> filterTasks(Map<Integer, Integer> counts) {
        checkAnswersPerTask();
        final List<Integer> ids = counts.entrySet().stream().
                filter(entry -> entry.getValue() < answersPerTask).
                map(Map.Entry::getKey).collect(Collectors.toList());
        Collections.shuffle(ids);
        ids.sort((id1, id2) -> counts.get(id1).compareTo(counts.get(id2)));
        return ids;
    }

    private void checkAnswersPerTask() {
        if (this.answersPerTask != null) return;
        this.answersPerTask = checkNotNull(Integer.parseInt(process.get().getOptions().get("answersPerTask")), "answersPerTask option is not set");
    }
}
