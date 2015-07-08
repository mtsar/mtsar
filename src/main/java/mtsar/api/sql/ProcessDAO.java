package mtsar.api.sql;

import mtsar.api.ProcessDefinition;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(ProcessDefinitionMapper.class)
public interface ProcessDAO {
    @SqlQuery("select * from processes")
    List<ProcessDefinition> select();

    @SqlQuery("select * from processes where name = :name limit 1")
    ProcessDefinition find(@Bind("name") String name);

    @SqlQuery("select count(*) from processes")
    int count();

    @SqlQuery("insert into processes (id, description, worker_ranker, task_allocator, answer_aggregator, options, datetime) values (:id, :description, :workerRanker, :taskAllocator, :answerAggregator, cast(:optionsJSON as jsonb), coalesce(:dateTime, localtimestamp)) returning id")
    String insert(@BindBean ProcessDefinition t);

    @SqlUpdate("delete from processes")
    void deleteAll();

    void close();
}
