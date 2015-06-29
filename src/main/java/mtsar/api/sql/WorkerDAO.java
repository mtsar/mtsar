package mtsar.api.sql;

import mtsar.api.Worker;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(WorkerMapper.class)
public interface WorkerDAO {
    @SqlQuery("select * from workers where process = :process")
    List<Worker> listForProcess(@Bind("process") String process);

    @SqlQuery("select * from workers where id = :id and process = :process limit 1")
    Worker find(@Bind("id") Integer id, @Bind("process") String process);

    @SqlQuery("select * from workers where external_id = :externalId and process = :process limit 1")
    Worker findByExternalId(@Bind("externalId") String externalId, @Bind("process") String process);

    @SqlQuery("insert into workers (process, external_id, datetime) values (:process, :externalId, :dateTime) returning id")
    int insert(@BindBean Worker t);

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

    void close();
}
