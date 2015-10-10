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

import mtsar.api.Process;
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

@RegisterMapper(ProcessDAO.Mapper.class)
public interface ProcessDAO {
    @SqlQuery("select * from processes order by datetime")
    List<Process.Definition> select();

    @SqlQuery("select * from processes where name = :name limit 1")
    Process.Definition find(@Bind("name") String name);

    @SqlQuery("select count(*) from processes")
    int count();

    @SqlQuery("insert into processes (id, description, worker_ranker, task_allocator, answer_aggregator, options, datetime) values (:id, :description, :workerRanker, :taskAllocator, :answerAggregator, cast(:optionsJSON as jsonb), coalesce(:dateTime, localtimestamp)) returning id")
    String insert(@BindBean Process.Definition t);

    @SqlUpdate("delete from processes where id = :id")
    void delete(@Bind("id") String id);

    @SqlUpdate("delete from processes")
    void deleteAll();

    void close();

    class Mapper implements ResultSetMapper<Process.Definition> {
        @Override
        public Process.Definition map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            final Process.Definition.Builder builder = new Process.Definition.Builder().
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
