package mtsar;

import io.dropwizard.util.JarLocation;

import javax.inject.Singleton;

@Singleton
public class MechanicalTsarVersion {
    private final JarLocation location;

    public MechanicalTsarVersion() {
        this.location = new JarLocation(getClass());
    }

    public boolean isAvailable() {
        return location.getVersion().isPresent();
    }

    public String getVersion() {
        return location.getVersion().or("SNAPSHOT");
    }

    public String toString() {
        return getVersion();
    }
}
