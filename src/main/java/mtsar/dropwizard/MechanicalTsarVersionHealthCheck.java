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
