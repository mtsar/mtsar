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

package mtsar.processors.worker;

import mtsar.api.Worker;
import mtsar.api.WorkerRanking;
import mtsar.processors.WorkerRanker;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class ZeroRanker implements WorkerRanker {
    @Override
    @Nonnull
    public Map<Integer, WorkerRanking> rank(@Nonnull Collection<Worker> workers) {
        return workers.stream().collect(Collectors.toMap(Worker::getId,
                worker -> new WorkerRanking.Builder().setWorker(worker).build()
        ));
    }
}
