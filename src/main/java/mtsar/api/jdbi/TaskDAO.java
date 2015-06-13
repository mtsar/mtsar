package mtsar.api.jdbi;

import mtsar.api.Task;
import mtsar.api.Worker;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(TaskMapper.class)
public interface TaskDAO {
    @SqlQuery("select * from tasks where process = :process")
    List<Task> listForProcess(@Bind("process") String process);

    @SqlQuery("select * from tasks where id = :id and process = :process limit 1")
    Task find(@Bind("id") Integer id, @Bind("process") String process);

    @SqlQuery("select * from tasks where process = :process order by random() limit 1")
    Task random(@Bind("process") String process);

    @SqlQuery("select count(*) from tasks")
    int count();

    @SqlQuery("select count(*) from tasks where process = :process")
    int count(@Bind("process") String process);

    @SqlQuery("insert into tasks (type, process, external_id, description, answers, datetime) values (:type, :process, :externalId, :description, cast(:answersTextArray as text[]), :dateTime) returning id")
    int insert(@BindBean Task t);

    @SqlUpdate("delete from tasks")
    void deleteAll();

    @SqlUpdate("select setval('tasks_id_seq', (select max(id) from tasks))")
    void reset();

    void close();
}
