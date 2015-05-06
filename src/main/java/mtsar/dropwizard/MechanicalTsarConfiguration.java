package mtsar.dropwizard;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import mtsar.api.Process;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class MechanicalTsarConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    private Map<String, Process> processes;

    public Map<String, Process> getProcesses() { return processes; }

    @Valid
    @NotNull
    @JsonProperty
    private DataSourceFactory database = new DataSourceFactory();

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
}