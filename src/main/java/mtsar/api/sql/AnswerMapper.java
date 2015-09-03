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

import mtsar.api.Answer;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class AnswerMapper implements ResultSetMapper<Answer> {
    public Answer map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Answer.Builder().
                setId(r.getInt("id")).
                setProcess(r.getString("process")).
                setDateTime(r.getTimestamp("datetime")).
                addAllTags(Arrays.asList((String[]) r.getArray("tags").getArray())).
                setType(r.getString("type")).
                setWorkerId(r.getInt("worker_id")).
                setTaskId(r.getInt("task_id")).
                addAllAnswers(Arrays.asList((String[]) r.getArray("answers").getArray())).
                build();
    }
}