package mtsar.api.sql;

import mtsar.api.Answer;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(AnswerMapper.class)
public interface AnswerDAO {
    @SqlQuery("select * from answers where process = :process")
    List<Answer> listForProcess(@Bind("process") String process);

    @SqlQuery("select * from answers where task_id = :taskId and process = :process")
    List<Answer> listForTask(@Bind("taskId") Integer taskId, @Bind("process") String process);

    @SqlQuery("select * from answers where worker_id = :workerId and process = :process")
    List<Answer> listForWorker(@Bind("workerId") Integer workerId, @Bind("process") String process);

    @SqlQuery("select * from answers where id = :id and process = :process limit 1")
    Answer find(@Bind("id") Integer id, @Bind("process") String process);

    @SqlQuery("insert into answers (process, external_id, worker_id, task_id, answers, datetime) values (:process, :externalId, :workerId, :taskId, cast(:answersTextArray as text[]), :dateTime) returning id")
    int insert(@BindBean Answer a);

    @SqlQuery("select count(*) from answers")
    int count();

    @SqlQuery("select count(*) from answers where process = :process")
    int count(@Bind("process") String process);

    @SqlUpdate("delete from answers where id = :id and process = :process")
    void delete(@Bind("id") Integer id, @Bind("process") String process);

    @SqlUpdate("delete from answers where process = :process")
    void deleteAll(@Bind("process") String process);

    @SqlUpdate("delete from answers")
    void deleteAll();

    void close();
}
