package mtsar.api.jdbi;

import mtsar.api.Event;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventMapper implements ResultSetMapper<Event> {
    public Event map(int index, ResultSet r, StatementContext ctx) throws SQLException
    {
        return new Event();
    }
}
