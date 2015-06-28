package mtsar.api.sql;

import mtsar.api.Answer;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AnswerMapper implements ResultSetMapper<Answer> {
    public Answer map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return Answer.builder().
                setId(r.getInt("id")).
                setExternalId(r.getString("external_id")).
                setProcess(r.getString("process")).
                setWorkerId(r.getInt("worker_id")).
                setTaskId(r.getInt("task_id")).
                setAnswer(r.getString("answer")).
                setDateTime(r.getTimestamp("datetime")).
                build();
    }
}