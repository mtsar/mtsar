package mtsar.api.sql;

import mtsar.api.Task;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class TaskMapper implements ResultSetMapper<Task> {
    public Task map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        return new Task.Builder().
                setId(r.getInt("id")).
                setProcess(r.getString("process")).
                setDateTime(r.getTimestamp("datetime")).
                addAllTags(Arrays.asList((String[]) r.getArray("tags").getArray())).
                setType(r.getString("type")).
                setDescription(r.getString("description")).
                addAllAnswers(Arrays.asList((String[]) r.getArray("answers").getArray())).
                build();
    }
}
