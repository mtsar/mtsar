package mtsar.dropwizard;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import mtsar.api.ProcessDefinition;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class MechanicalTsarConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty("processes")
    private Map<String, ProcessDefinition> definitions;

    public Map<String, ProcessDefinition> getDefinitions() {
        return definitions;
    }

    @Valid
    @NotNull
    @JsonProperty
    private DataSourceFactory database = new DataSourceFactory();

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
}