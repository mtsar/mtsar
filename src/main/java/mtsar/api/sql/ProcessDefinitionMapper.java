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
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProcessDefinitionMapper implements ResultSetMapper<ProcessDefinition> {
    @Override
    public ProcessDefinition map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        final ProcessDefinition.Builder builder = new ProcessDefinition.Builder().
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
