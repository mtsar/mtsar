package mtsar.processors;

import mtsar.api.Process;

public abstract class Processor {
    protected Process process = null;

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
