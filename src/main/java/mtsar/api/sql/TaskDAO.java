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

import mtsar.api.Task;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.unstable.BindIn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@UseStringTemplate3StatementLocator
@RegisterMapper(TaskDAO.Mapper.class)
public interface TaskDAO {
    String TASK_TYPE_SINGLE = "single";

    @SqlQuery("select * from tasks where stage = :stage")
    List<Task> listForStage(@Bind("stage") String stage);

    @SqlQuery("select * from tasks where id = :id and stage = :stage limit 1")
    Task find(@Bind("id") Integer id, @Bind("stage") String stage);

    @SqlQuery("select * from tasks where id in (<ids>) and stage = :stage")
    List<Task> select(@BindIn("ids") List<Integer> ids, @Bind("stage") String stage);

    @SqlQuery("select * from tasks where stage = :stage order by random() limit 1")
    Task random(@Bind("stage") String stage);

    @SqlQuery("select count(*) from tasks")
    int count();

    @SqlQuery("select count(*) from tasks where stage = :stage")
    int count(@Bind("stage") String stage);

    /**
     * Provides the upper bound of unanswered tasks of the given stage by the given worker.
     *
     * @param stage    the stage name.
     * @param workerId the worker identifier.
     * @return the number of unanswered tasks.
     */
    @SqlQuery("select count(distinct tasks.id) from tasks left join answers on answers.task_id = tasks.id and answers.stage = tasks.stage and answers.worker_id = :worker_id where tasks.stage = :stage and answers.id is null")
    int remaining(@Bind("stage") String stage, @Bind("worker_id") Integer workerId);

    @SqlQuery("insert into tasks (stage, datetime, tags, type, description, answers) values (:stage, coalesce(:dateTime, localtimestamp), cast(:tagsTextArray as text[]), cast(:type as task_type), :description, cast(:answersTextArray as text[])) returning id")
    int insert(@BindBean Task t);

    @SqlBatch("insert into tasks (id, stage, datetime, tags, type, description, answers) values (coalesce(:id, nextval('tasks_id_seq')), :stage, coalesce(:dateTime, localtimestamp), cast(:tagsTextArray as text[]), cast(:type as task_type), :description, cast(:answersTextArray as text[]))")
    @BatchChunkSize(1000)
    void insert(@BindBean Iterator<Task> tasks);

    @SqlUpdate("begin transaction; delete from answers where task_id = :id and stage = :stage; delete from tasks where id = :id and stage = :stage; commit")
    void delete(@Bind("id") Integer id, @Bind("stage") String stage);

    @SqlUpdate("delete from tasks where stage = :stage")
    void deleteAll(@Bind("stage") String stage);

    @SqlUpdate("delete from tasks")
    void deleteAll();

    @SqlUpdate("select setval('tasks_id_seq', coalesce((select max(id) + 1 from tasks), 1), false)")
    void resetSequence();

    void close();

    class Mapper implements ResultSetMapper<Task> {
        public Task map(int index, ResultSet r, StatementContext ctx) throws SQLException {

            return new Task.Builder().
                    setId(r.getInt("id")).
                    setStage(r.getString("stage")).
                    setDateTime(r.getTimestamp("datetime")).
                    addAllTags(Arrays.asList((String[]) r.getArray("tags").getArray())).
                    setMetadata(r.getString("metadata")).
                    setType(r.getString("type")).
                    setDescription(r.getString("description")).
                    addAllAnswers(Arrays.asList((String[]) r.getArray("answers").getArray())).
                    build();
        }
    }
}
