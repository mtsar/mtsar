package mtsar.dropwizard;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import mtsar.api.Process;
import mtsar.api.jdbi.AnswerDAO;
import mtsar.api.jdbi.EventDAO;
import mtsar.api.jdbi.TaskDAO;
import mtsar.api.jdbi.WorkerDAO;
import org.skife.jdbi.v2.DBI;

import java.util.Map;
import java.util.logging.Logger;

public class MechanicalTsarModule extends AbstractModule {
    protected DBI dbi = null;

    @Override
    protected void configure() {
    }

    @Provides
    public Map<String, Process> getProcesses(MechanicalTsarConfiguration configuration) {
        return configuration.getProcesses();
    }

    @Provides
    synchronized public DBI provideDBI(DBIFactory factory, Environment environment, MechanicalTsarConfiguration configuration, Logger logger) throws ClassNotFoundException {
        if (dbi == null) {
            logger.info(String.format("Providing a DBI"));
            dbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");
        }
        return dbi;
    }

    @Provides
    public WorkerDAO provideWorkerDAO(DBI jdbi, Logger logger) {
        logger.info(String.format("Providing a WorkerDAO"));
        return jdbi.onDemand(WorkerDAO.class);
    }

    @Provides
    public TaskDAO provideTaskDAO(DBI jdbi, Logger logger) {
        logger.info(String.format("Providing a TaskDAO"));
        return jdbi.onDemand(TaskDAO.class);
    }

    @Provides
    public AnswerDAO provideAnswerDAO(DBI jdbi, Logger logger) {
        logger.info(String.format("Providing an AnswerDAO"));
        return jdbi.onDemand(AnswerDAO.class);
    }

    @Provides
    public EventDAO provideEventDAO(DBI jdbi, Logger logger) {
        logger.info(String.format("Providing an EventDAO"));
        return jdbi.onDemand(EventDAO.class);
    }
}
