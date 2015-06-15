package mtsar.dropwizard.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import mtsar.api.jdbi.AnswerDAO;
import mtsar.api.jdbi.EventDAO;
import mtsar.api.jdbi.TaskDAO;
import mtsar.api.jdbi.WorkerDAO;
import mtsar.dropwizard.MechanicalTsarConfiguration;
import org.skife.jdbi.v2.DBI;

import javax.inject.Singleton;
import java.util.logging.Logger;

/**
 * Of course, this module is a crutch. Do not miss the opportunity of receiving a $5 prize
 * for letting me know how to maintain the database connection outside Guice.
 */
public class DBIModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public DBI provideDBI(DBIFactory factory, Environment environment, MechanicalTsarConfiguration configuration, Logger logger) throws ClassNotFoundException {
        logger.info(String.format("Providing a DBI"));
        return factory.build(environment, configuration.getDataSourceFactory(), "postgresql");
    }

    @Provides
    @Singleton
    public WorkerDAO provideWorkerDAO(DBI jdbi, Logger logger) {
        logger.info(String.format("Providing a WorkerDAO"));
        return jdbi.onDemand(WorkerDAO.class);
    }

    @Provides
    @Singleton
    public TaskDAO provideTaskDAO(DBI jdbi, Logger logger) {
        logger.info(String.format("Providing a TaskDAO"));
        return jdbi.onDemand(TaskDAO.class);
    }

    @Provides
    @Singleton
    public AnswerDAO provideAnswerDAO(DBI jdbi, Logger logger) {
        logger.info(String.format("Providing an AnswerDAO"));
        return jdbi.onDemand(AnswerDAO.class);
    }

    @Provides
    @Singleton
    public EventDAO provideEventDAO(DBI jdbi, Logger logger) {
        logger.info(String.format("Providing an EventDAO"));
        return jdbi.onDemand(EventDAO.class);
    }
}
