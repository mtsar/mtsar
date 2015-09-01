package mtsar.api.sql;

import mtsar.api.Worker;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class WorkerMapper implements ResultSetMapper<Worker> {
    public Worker map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Worker.Builder().
                setId(r.getInt("id")).
                setProcess(r.getString("process")).
                setDateTime(r.getTimestamp("datetime")).
                addAllTags(Arrays.asList((String[]) r.getArray("tags").getArray())).
                build();
    }
}
