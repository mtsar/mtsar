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

import mtsar.api.*;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.TaskAllocator;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class InverseCountAllocator implements TaskAllocator {
    public static final Comparator<Triple<Integer, Integer, Double>> INVERSE_COUNT = Comparator.comparing(Triple<Integer, Integer, Double>::getMiddle).thenComparing(Triple::getRight);
    protected final Provider<Stage> stage;
    protected final DBI dbi;
    protected final TaskDAO taskDAO;
    protected final AnswerDAO answerDAO;
    protected final CountDAO countDAO;

    @Inject
    public InverseCountAllocator(Provider<Stage> stage, DBI dbi, TaskDAO taskDAO, AnswerDAO answerDAO) {
        this.stage = requireNonNull(stage);
        this.dbi = requireNonNull(dbi);
        this.taskDAO = requireNonNull(taskDAO);
        this.answerDAO = requireNonNull(answerDAO);
        this.countDAO = requireNonNull(dbi.onDemand(CountDAO.class));
    }

    @Override
    @Nonnull
    public Optional<TaskAllocation> allocate(@Nonnull Worker worker, @Nonnegative int n) {
        requireNonNull(stage.get(), "the stage provider should not provide null");
        final Set<Integer> answered = answerDAO.listForWorker(worker.getId(), stage.get().getId()).stream().
                map(Answer::getTaskId).collect(Collectors.toSet());

        final Map<Integer, Integer> counts = countDAO.getCountsSQL(stage.get().getId()).stream().
                filter(pair -> !answered.contains(pair.getKey())).
                collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        final List<Integer> ids = filterTasks(counts);
        final int taskRemaining = ids.size();

        if (ids.isEmpty()) return Optional.empty();
        if (taskRemaining > n) ids.subList(n, ids.size()).clear();
        final List<Task> tasks = taskDAO.select(ids, stage.get().getId());

        final int taskCount = taskDAO.count(stage.get().getId());
        final TaskAllocation allocation = new TaskAllocation.Builder().
                setWorker(worker).
                addAllTasks(tasks).
                setTaskRemaining(taskRemaining).
                setTaskCount(taskCount).
                build();
        return Optional.of(allocation);
    }

    protected List<Integer> filterTasks(Map<Integer, Integer> counts) {
        return counts.entrySet().stream().
                map(entry -> Triple.of(entry.getKey(), entry.getValue(), Math.random())).
                sorted(INVERSE_COUNT).
                map(Triple::getLeft).
                collect(Collectors.toList());
    }

    @RegisterMapper(CountDAO.Mapper.class)
    public interface CountDAO {
        @SqlQuery("select tasks.id, count(answers.id) from tasks left join answers on answers.task_id = tasks.id and answers.stage = tasks.stage and answers.type <> 'skip' where tasks.stage = :stage group by tasks.id")
        List<Pair<Integer, Integer>> getCountsSQL(@Bind("stage") String stage);

        class Mapper implements ResultSetMapper<Pair> {
            public Pair<Integer, Integer> map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                return Pair.of(r.getInt("id"), r.getInt("count"));
            }
        }
    }
}
