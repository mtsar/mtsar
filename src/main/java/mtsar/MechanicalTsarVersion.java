package mtsar;

import io.dropwizard.util.JarLocation;

import javax.inject.Singleton;

/**
 * Mechanical Tsar version wrapper.
 */
@Singleton
public class MechanicalTsarVersion {
    private final JarLocation location;

    /**
     * Create a new instance of MechanicalTsarVersion.
     */
    public MechanicalTsarVersion() {
        this.location = new JarLocation(getClass());
    }

    /**
     * Check the availability of the actual version.
     * @return true if the version information is present in the classpath.
     */
    public boolean isAvailable() {
        return location.getVersion().isPresent();
    }

    /**
     * Get the actual version.
     * @return the Mechanical Tsar version or "SNAPSHOT".
     */
    public String getVersion() {
        return location.getVersion().or("SNAPSHOT");
    }

    /**
     * Get the actual version.
     * @see #getVersion
     * @return the Mechanical Tsar version or "SNAPSHOT".
     */
    public String toString() {
        return getVersion();
    }
}
