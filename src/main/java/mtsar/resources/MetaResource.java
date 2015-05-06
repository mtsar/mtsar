package mtsar.resources;

import mtsar.MechanicalTsarVersion;
import mtsar.api.Process;
import mtsar.views.DashboardView;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Singleton
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class MetaResource {
    final MechanicalTsarVersion version;
    final Map<String, Process> processes;

    @Inject
    public MetaResource(MechanicalTsarVersion version, Map<String, Process> processes) {
        this.version = version;
        this.processes = processes;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public DashboardView getDashboardView() {
        return new DashboardView(version, processes);
    }

    @GET
    @Path("version")
    @Produces(MediaType.TEXT_PLAIN)
    public String getVersion() {
        return version.getVersion();
    }
}
