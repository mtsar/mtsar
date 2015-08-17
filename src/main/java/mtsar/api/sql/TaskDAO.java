package mtsar.api.sql;

import mtsar.api.Task;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Iterator;
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

    /**
     * Provides the upper bound of unanswered tasks of the given process by the given worker.
     *
     * @param process  the process name.
     * @param workerId the worker identifier.
     * @return the number of unanswered tasks.
     */
    @SqlQuery("select count(distinct tasks.id) from tasks left join answers on answers.task_id = tasks.id and answers.process = tasks.process and answers.worker_id = :worker_id where tasks.process = :process and answers.id is null")
    int remaining(@Bind("process") String process, @Bind("worker_id") Integer workerId);

    @SqlQuery("insert into tasks (process, datetime, tags, type, description, answers) values (:process, coalesce(:dateTime, localtimestamp), cast(:tagsTextArray as text[]), cast(:type as task_type), :description, cast(:answersTextArray as text[])) returning id")
    int insert(@BindBean Task t);

    @SqlBatch("insert into tasks (id, process, datetime, tags, type, description, answers) values (coalesce(:id, nextval('tasks_id_seq')), :process, coalesce(:dateTime, localtimestamp), cast(:tagsTextArray as text[]), cast(:type as task_type), :description, cast(:answersTextArray as text[]))")
    @BatchChunkSize(1000)
    void insert(@BindBean Iterator<Task> tasks);

    @SqlUpdate("delete from tasks where id = :id and process = :process")
    void delete(@Bind("id") Integer id, @Bind("process") String process);

    @SqlUpdate("delete from tasks where process = :process")
    void deleteAll(@Bind("process") String process);

    @SqlUpdate("delete from tasks")
    void deleteAll();

    @SqlUpdate("select setval('tasks_id_seq', coalesce((select max(id) + 1 from tasks), 1), false)")
    void resetSequence();

    void close();
}
