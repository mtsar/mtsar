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