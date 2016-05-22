package mtsar.dropwizard.hk2;

import mtsar.api.Stage;
import mtsar.api.sql.StageDAO;
import mtsar.processors.AnswerAggregator;
import mtsar.processors.TaskAllocator;
import mtsar.processors.WorkerRanker;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.internal.inject.Injections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StageService {
    private final Logger logger;
    private final Map<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();
    private final StageDAO stageDAO;
    private final ServiceLocator locator;

    @Inject
    public StageService(StageDAO stageDAO, ServiceLocator locator) {
        this.logger = LoggerFactory.getLogger(getClass());
        this.stageDAO = stageDAO;
        this.locator = locator;
    }

    public Map<String, Stage> getStages() {
        final Map<String, Stage> map = new LinkedHashMap<>();
        final List<Stage.Definition> definitions = stageDAO.select();

        for (final Stage.Definition definition : definitions) {
            try {
                map.put(definition.getId(), getStage(definition));
            } catch (IllegalStateException e) {
                logger.error("Error initiating stage definition with id {}", definition.getId(), e);
            }
        }

        return map;
    }

    private static void initialize(ServiceLocator locator, Object bean) {
        locator.inject(bean);
        locator.postConstruct(bean);
    }

    private Stage getStage(Stage.Definition definition) {
        final Class<? extends WorkerRanker> workerRankerClass = getClass(definition.getWorkerRanker(), WorkerRanker.class);
        final Class<? extends TaskAllocator> taskAllocatorClass = getClass(definition.getTaskAllocator(), TaskAllocator.class);
        final Class<? extends AnswerAggregator> answerAggregatorClass = getClass(definition.getAnswerAggregator(), AnswerAggregator.class);

        final WorkerRanker workerRanker = locator.create(workerRankerClass);
        final TaskAllocator taskAllocator = locator.create(taskAllocatorClass);
        final AnswerAggregator answerAggregator = locator.create(answerAggregatorClass);
        final Stage stage = new Stage(definition, workerRanker, taskAllocator, answerAggregator);

        final ServiceLocator localLocator = Injections.createLocator(locator, new AbstractBinder() {
            @Override
            protected void configure() {
                bind(definition).to(Stage.Definition.class);
                bind(stage).to(Stage.class);
                bind(workerRanker).to(WorkerRanker.class);
                bind(taskAllocator).to(TaskAllocator.class);
                bind(answerAggregator).to(AnswerAggregator.class);
            }
        });

        initialize(localLocator, workerRanker);
        initialize(localLocator, taskAllocator);
        initialize(localLocator, answerAggregator);

        return localLocator.getService(Stage.class);
    }

    private <T> Class<? extends T> getClass(String name, Class<? extends T> tClass) {
        final Class<?> clazz = CLASS_CACHE.computeIfAbsent(name, n -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("No class with name " + name, e);
            }
        });
        return clazz.asSubclass(tClass);
    }
}
