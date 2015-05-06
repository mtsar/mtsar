package mtsar.views;

import io.dropwizard.views.View;
import mtsar.api.Process;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

public class ProcessView extends View {
    private final Process process;

    @Inject
    public ProcessView(Process process) {
        super("process.mustache");
        this.process = process;
    }

    public String getTitle() {
        return String.format("Process \"%s\"", process.getId());
    }

    public Process getProcess() {
        return process;
    }

    /**
     * By some strange reason, mustache can not access the options map,
     * but the present method works just fine.
     *
     * @return process options
     */
    public Collection<Map.Entry<String, Object>> getOptions() {
        return process.getOptions().entrySet();
    }

    public int getWorkerCount() {
        return process.getWorkerDAO().count(process.getId());
    }

    public int getTaskCount() {
        return process.getTaskDAO().count(process.getId());
    }

    public int getAnswerCount() {
        return process.getAnswerDAO().count(process.getId());
    }
}
