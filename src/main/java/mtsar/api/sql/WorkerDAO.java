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

import mtsar.api.Worker;
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
@RegisterMapper(WorkerDAO.Mapper.class)
public abstract class WorkerDAO {
    @SqlQuery("select * from workers where stage = :stage")
    public abstract List<Worker> listForStage(@Bind("stage") String stage);

    @SqlQuery("select * from workers where id = :id and stage = :stage limit 1")
    public abstract Worker find(@Bind("id") Integer id, @Bind("stage") String stage);

    @SqlQuery("select * from workers where stage = :stage and tags @\\> ARRAY[<tags>]\\:\\:text[] limit 1")
    public abstract Worker findByTags(@Bind("stage") String stage, @BindIn("tags") List<String> tags);

    @SqlQuery("insert into workers (stage, datetime, tags) values (:stage, coalesce(:dateTime, localtimestamp), cast(:tagsTextArray as text[])) returning id")
    public abstract int insert(@BindBean Worker t);

    @SqlBatch("insert into workers (id, stage, datetime, tags) values (coalesce(:id, nextval('workers_id_seq')), :stage, coalesce(:dateTime, localtimestamp), cast(:tagsTextArray as text[]))")
    @BatchChunkSize(1000)
    public abstract void insert(@BindBean Iterator<Worker> tasks);

    @SqlQuery("select count(*) from workers")
    public abstract int count();

    @SqlQuery("select count(*) from workers where stage = :stage")
    public abstract int count(@Bind("stage") String stage);

    @Transaction
    @SqlUpdate("delete from answers where worker_id = :id and stage = :stage; delete from workers where id = :id and stage = :stage")
    public abstract void delete(@Bind("id") Integer id, @Bind("stage") String stage);

    @SqlUpdate("delete from workers where stage = :stage")
    public abstract void deleteAll(@Bind("stage") String stage);

    @SqlUpdate("delete from workers")
    public abstract void deleteAll();

    @SqlUpdate("select setval('workers_id_seq', coalesce((select max(id) + 1 from workers), 1), false)")
    public abstract void resetSequence();

    public abstract void close();

    public static class Mapper implements ResultSetMapper<Worker> {
        public Worker map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new Worker.Builder().
                    setId(r.getInt("id")).
                    setStage(r.getString("stage")).
                    setDateTime(r.getTimestamp("datetime")).
                    addAllTags(Arrays.asList((String[]) r.getArray("tags").getArray())).
                    setMetadata(r.getString("metadata")).
                    build();
        }
    }
}
