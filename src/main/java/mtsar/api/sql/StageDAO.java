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

package mtsar.api.sql;

import mtsar.api.Stage;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RegisterMapper(StageDAO.Mapper.class)
public interface StageDAO {
    @SqlQuery("select * from stages where id = :id limit 1")
    Stage.Definition find(@Bind("id") String id);

    @SqlQuery("select * from stages order by datetime")
    List<Stage.Definition> select();

    @SqlQuery("select count(*) from stages")
    int count();

    @SqlQuery("insert into stages (id, description, worker_ranker, task_allocator, answer_aggregator, options, datetime) values (:id, :description, :workerRanker, :taskAllocator, :answerAggregator, cast(:optionsJSON as jsonb), coalesce(:dateTime, localtimestamp)) returning id")
    String insert(@BindBean Stage.Definition definition);

    @SqlUpdate("update stages set description=:description, worker_ranker=:workerRanker, task_allocator=:taskAllocator, answer_aggregator=:answerAggregator, options=cast(:optionsJSON as jsonb) where id=:id")
    void update(@BindBean Stage.Definition definition);

    @SqlUpdate("delete from stages where id = :id")
    void delete(@Bind("id") String id);

    @SqlUpdate("delete from stages")
    void deleteAll();

    void close();

    class Mapper implements ResultSetMapper<Stage.Definition> {
        @Override
        public Stage.Definition map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            final Stage.Definition.Builder builder = new Stage.Definition.Builder().
                    setId(r.getString("id")).
                    setDescription(r.getString("description")).
                    setWorkerRanker(r.getString("worker_ranker")).
                    setTaskAllocator(r.getString("task_allocator")).
                    setAnswerAggregator(r.getString("answer_aggregator")).
                    setOptions(r.getString("options")).
                    setDateTime(r.getTimestamp("datetime"));
            return builder.build();
        }
    }
}
