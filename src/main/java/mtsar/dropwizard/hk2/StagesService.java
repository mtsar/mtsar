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
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StagesService {
    private static final Logger log = LoggerFactory.getLogger(StagesService.class);
    private final Map<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();
    private final StageDAO stageDAO;
    private final ServiceLocator locator;

    @Inject
    public StagesService(StageDAO stageDAO, ServiceLocator locator) {
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
                log.error("Error initiating stage definition with id {}", definition.getId(), e);
            }
        }
        return map;
    }


    public Stage.Definition createOrUpdate(String stageId, String description, String workerRanker, String taskAllocator, String answerAggregator) {
        Stage.Definition initDef = stageDAO.select(stageId);
        Stage.Definition.Builder builder;
        if (initDef == null) {
            builder = new Stage.Definition.Builder()
                    .setId(stageId)
                    .setDateTime(new Timestamp(System.currentTimeMillis()));
        } else {
            builder = Stage.Definition.Builder.from(initDef);
        }
        if (description != null) {
            builder.setDescription(description);
        }
        if (answerAggregator != null) {
            builder.setAnswerAggregator(answerAggregator);
        }
        if (taskAllocator != null) {
            builder.setTaskAllocator(taskAllocator);
        }
        if (workerRanker != null) {
            builder.setWorkerRanker(workerRanker);
        }
        Stage.Definition resultDef = builder.build();
        if (initDef == null) {
            stageDAO.insert(resultDef);
        } else {
            stageDAO.update(resultDef);
        }
        return resultDef;
    }

    private static void initialize(ServiceLocator locator, Object bean) {
        locator.inject(bean);
        locator.postConstruct(bean);
    }

    private Stage getStage(Stage.Definition definition) {
        final Class<? extends WorkerRanker> rankerClass = getClass(definition.getWorkerRanker(), WorkerRanker.class);
        final Class<? extends TaskAllocator> allocatorClass = getClass(definition.getTaskAllocator(), TaskAllocator.class);
        final Class<? extends AnswerAggregator> aggregatorClass = getClass(definition.getAnswerAggregator(), AnswerAggregator.class);
        final WorkerRanker ranker = locator.create(rankerClass);
        final TaskAllocator allocator = locator.create(allocatorClass);
        final AnswerAggregator aggregator = locator.create(aggregatorClass);
        final Stage stage = new Stage(definition, ranker, allocator, aggregator);
        final ServiceLocator localLocator = Injections.createLocator(locator, new AbstractBinder() {
            @Override
            protected void configure() {
                bind(definition).to(Stage.Definition.class);
                bind(ranker).to(WorkerRanker.class);
                bind(allocator).to(TaskAllocator.class);
                bind(aggregator).to(AnswerAggregator.class);
                bind(stage).to(Stage.class);
            }
        });
        initialize(localLocator, ranker);
        initialize(localLocator, allocator);
        initialize(localLocator, aggregator);
        return localLocator.getService(Stage.class);
    }

    private <T> Class<? extends T> getClass(String name, Class<? extends T> tClass) {
        Class<?> clazz = CLASS_CACHE.computeIfAbsent(name, n -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("No class with name " + name, e);
            }
        });
        return clazz.asSubclass(tClass);
    }
}
