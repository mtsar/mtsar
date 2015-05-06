package mtsar.api.jdbi;

import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(EventMapper.class)
public interface EventDAO {
    void close();
}
