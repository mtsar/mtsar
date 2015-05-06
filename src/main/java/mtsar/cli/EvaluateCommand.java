package mtsar.cli;

import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import mtsar.api.Task;
import mtsar.api.jdbi.TaskDAO;
import mtsar.dropwizard.MechanicalTsarApplication;
import mtsar.dropwizard.MechanicalTsarConfiguration;
import net.sourceforge.argparse4j.inf.Namespace;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class EvaluateCommand extends EnvironmentCommand<MechanicalTsarConfiguration> {
    private final MechanicalTsarApplication application;

    public EvaluateCommand(Application<MechanicalTsarConfiguration> application) {
        super(application, "evaluate", "Evaluates the system");
        this.application = (MechanicalTsarApplication) application;
    }

    protected void run(Environment environment, Namespace namespace, MechanicalTsarConfiguration configuration) {
        final TaskDAO dao = application.getInjector().getInstance(TaskDAO.class);
        dao.deleteAll();
        dao.reset();
        dao.insert(Task.builder().
                setId(1).
                setDateTime(Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())).
                build());
        Task t = dao.random(null);
        System.out.println(toString());
    }
}