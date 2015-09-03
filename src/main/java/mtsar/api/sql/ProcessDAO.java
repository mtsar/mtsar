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

import mtsar.api.ProcessDefinition;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(ProcessDefinitionMapper.class)
public interface ProcessDAO {
    @SqlQuery("select * from processes order by datetime")
    List<ProcessDefinition> select();

    @SqlQuery("select * from processes where name = :name limit 1")
    ProcessDefinition find(@Bind("name") String name);

    @SqlQuery("select count(*) from processes")
    int count();

    @SqlQuery("insert into processes (id, description, worker_ranker, task_allocator, answer_aggregator, options, datetime) values (:id, :description, :workerRanker, :taskAllocator, :answerAggregator, cast(:optionsJSON as jsonb), coalesce(:dateTime, localtimestamp)) returning id")
    String insert(@BindBean ProcessDefinition t);

    @SqlUpdate("delete from processes where id = :id")
    void delete(@Bind("id") String id);

    @SqlUpdate("delete from processes")
    void deleteAll();

    void close();
}
