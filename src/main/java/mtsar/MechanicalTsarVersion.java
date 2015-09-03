/*
 * Copyright 2015 Dmitry Ustalov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
