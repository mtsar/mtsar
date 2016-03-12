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
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@UseStringTemplate3StatementLocator
@RegisterMapper(AnswerDAO.Mapper.class)
public interface AnswerDAO {
    String ANSWER_TYPE_ANSWER = "answer";
    String ANSWER_TYPE_DEFAULT = ANSWER_TYPE_ANSWER;
    String ANSWER_TYPE_SKIP = "skip";

    @SqlQuery("select * from answers where stage = :stage")
    List<Answer> listForStage(@Bind("stage") String stage);

    @SqlQuery("select * from answers where task_id = :taskId and stage = :stage")
    List<Answer> listForTask(@Bind("taskId") Integer taskId, @Bind("stage") String stage);

    @SqlQuery("select * from answers where worker_id = :workerId and stage = :stage")
    List<Answer> listForWorker(@Bind("workerId") Integer workerId, @Bind("stage") String stage);

    @SqlQuery("select * from answers where id = :id and stage = :stage limit 1")
    Answer find(@Bind("id") Integer id, @Bind("stage") String stage);

    @SqlQuery("select * from answers where stage = :stage and worker_id = :worker_id and task_id = :task_id limit 1")
    Answer findByWorkerAndTask(@Bind("stage") String stage, @Bind("worker_id") Integer workerId, @Bind("task_id") Integer taskId);

    @SqlQuery("insert into answers (stage, datetime, tags, type, worker_id, task_id, answers) values (:stage, coalesce(:dateTime, localtimestamp), cast(:tagsTextArray as text[]), cast(:type as answer_type), :workerId, :taskId, cast(:answersTextArray as text[])) returning id")
    int insert(@BindBean Answer a);

    @SqlBatch("insert into answers (id, stage, datetime, tags, type, worker_id, task_id, answers) values (coalesce(:id, nextval('answers_id_seq')), :stage, coalesce(:dateTime, localtimestamp), cast(:tagsTextArray as text[]), cast(:type as answer_type), :workerId, :taskId, cast(:answersTextArray as text[]))")
    @BatchChunkSize(1000)
    int[] insert(@BindBean Iterator<Answer> answers);

    /*
     * This is a slow method for inserting the given collection of answers and returning all the inserted objects.
     */
    static List<Answer> insert(AnswerDAO dao, Collection<Answer> answers) {
        return answers.stream().map(answer -> dao.find(dao.insert(answer), answer.getStage())).collect(Collectors.toList());
    }

    @SqlQuery("select count(*) from answers")
    int count();

    @SqlQuery("select count(*) from answers where stage = :stage")
    int count(@Bind("stage") String stage);

    @SqlUpdate("delete from answers where id = :id and stage = :stage")
    void delete(@Bind("id") Integer id, @Bind("stage") String stage);

    @SqlUpdate("delete from answers where stage = :stage")
    void deleteAll(@Bind("stage") String stage);

    @SqlUpdate("delete from answers")
    void deleteAll();

    @SqlUpdate("select setval('answers_id_seq', coalesce((select max(id) + 1 from answers), 1), false)")
    void resetSequence();

    void close();

    class Mapper implements ResultSetMapper<Answer> {
        @Override
        public Answer map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new Answer.Builder().
                    setId(r.getInt("id")).
                    setStage(r.getString("stage")).
                    setDateTime(r.getTimestamp("datetime")).
                    addAllTags(Arrays.asList((String[]) r.getArray("tags").getArray())).
                    setMetadata(r.getString("metadata")).
                    setType(r.getString("type")).
                    setWorkerId(r.getInt("worker_id")).
                    setTaskId(r.getInt("task_id")).
                    addAllAnswers(Arrays.asList((String[]) r.getArray("answers").getArray())).
                    build();
        }
    }
}
