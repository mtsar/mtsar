package mtsar;

import javax.inject.Singleton;

import io.dropwizard.util.JarLocation;

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
