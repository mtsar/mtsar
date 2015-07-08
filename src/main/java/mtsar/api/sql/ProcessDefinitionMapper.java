package mtsar.api.sql;

import mtsar.api.ProcessDefinition;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProcessDefinitionMapper implements ResultSetMapper<ProcessDefinition> {
    @Override
    public ProcessDefinition map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        final ProcessDefinition.Builder builder = ProcessDefinition.builder().
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
