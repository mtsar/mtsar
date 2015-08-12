package mtsar.api.sql;

import mtsar.api.Worker;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Iterator;
import java.util.List;

@RegisterMapper(WorkerMapper.class)
public interface WorkerDAO {
    @SqlQuery("select * from workers where process = :process")
    List<Worker> listForProcess(@Bind("process") String process);

    @SqlQuery("select * from workers where id = :id and process = :process limit 1")
    Worker find(@Bind("id") Integer id, @Bind("process") String process);

    @SqlQuery("select * from workers where process = :process and tags @> cast(ARRAY[:tag] as text[]) limit 1")
    Worker findByTag(@Bind("process") String process, @Bind("tag") String tag);

    @SqlQuery("insert into workers (process, datetime, tags) values (:process, coalesce(:dateTime, localtimestamp), cast(:tagsTextArray as text[])) returning id")
    int insert(@BindBean Worker t);

    @SqlBatch("insert into workers (id, process, datetime, tags) values (coalesce(:id, nextval('workers_id_seq')), :process, coalesce(:dateTime, localtimestamp), cast(:tagsTextArray as text[]))")
    @BatchChunkSize(1000)
    void insert(@BindBean Iterator<Worker> tasks);

    @SqlQuery("select count(*) from workers")
    int count();

    @SqlQuery("select count(*) from workers where process = :process")
    int count(@Bind("process") String process);

    @SqlUpdate("delete from workers where where id = :id and process = :process")
    void delete(@Bind("id") Integer id, @Bind("process") String process);

    @SqlUpdate("delete from workers where process = :process")
    void deleteAll(@Bind("process") String process);

    @SqlUpdate("delete from workers")
    void deleteAll();

    @SqlUpdate("select setval('workers_id_seq', coalesce((select max(id) + 1 from workers), 1), false)")
    void resetSequence();

    void close();
}
