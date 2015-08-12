package mtsar.api.sql;

import mtsar.api.Task;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskMapper implements ResultSetMapper<Task> {
    public Task map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return Task.builder().
                setId(r.getInt("id")).
                setProcess(r.getString("process")).
                setDateTime(r.getTimestamp("datetime")).
                setTags((String[]) r.getArray("tags").getArray()).
                setType(r.getString("type")).
                setDescription(r.getString("description")).
                setAnswers((String[]) r.getArray("answers").getArray()).
                build();
    }
}
