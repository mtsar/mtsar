package mtsar.dropwizard;

import com.codahale.metrics.health.HealthCheck;
import mtsar.MechanicalTsarVersion;

import javax.inject.Inject;

public class MechanicalTsarVersionHealthCheck extends HealthCheck {
    private final MechanicalTsarVersion version;

    @Inject
    public MechanicalTsarVersionHealthCheck(MechanicalTsarVersion version) {
        this.version = version;
    }

    protected Result check() throws Exception {
        if (version.isAvailable()) {
            return Result.healthy(version.getVersion());
        } else {
            return Result.unhealthy("Could not reach the package version");
        }
    }
}
