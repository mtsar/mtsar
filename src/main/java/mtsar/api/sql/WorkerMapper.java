package mtsar.api.sql;

import mtsar.api.Worker;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkerMapper implements ResultSetMapper<Worker> {
    public Worker map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return Worker.builder().
                setId(r.getInt("id")).
                setProcess(r.getString("process")).
                setDateTime(r.getTimestamp("datetime")).
                setTags((String[]) r.getArray("tags").getArray()).
                build();
    }
}
