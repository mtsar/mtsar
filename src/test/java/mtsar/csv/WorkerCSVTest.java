/*
 * Copyright 2016 Dmitry Ustalov
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

package mtsar.csv;

import com.google.common.collect.Lists;
import mtsar.api.Stage;
import mtsar.api.Worker;
import mtsar.api.csv.WorkerCSV;
import org.apache.commons.csv.CSVParser;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static mtsar.TestHelper.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WorkerCSVTest {
    private static final Stage stage = mock(Stage.class);
    private static final Worker worker1 = fixture("worker1.json", Worker.class);
    private static final Worker worker2 = fixture("worker2.json", Worker.class);
    private static final Collection<Worker> workers = Arrays.asList(worker1, worker2);

    @Before
    public void setup() {
        when(stage.getId()).thenReturn("1");
    }

    @Test
    public void testCSV() throws IOException {
        try (final PipedInputStream pis = new PipedInputStream()) {
            try (final PipedOutputStream pos = new PipedOutputStream(pis)) {
                WorkerCSV.write(workers, pos);

                try (final Reader reader = new InputStreamReader(pis, StandardCharsets.UTF_8)) {
                    try (final CSVParser csv = new CSVParser(reader, WorkerCSV.FORMAT)) {
                        final List<Worker> parsed = Lists.newArrayList(WorkerCSV.parse(stage, csv));
                        assertThat(parsed).hasSize(2);
                        assertThat(parsed).usingElementComparatorIgnoringFields("dateTime").isEqualTo(workers);
                    }
                }
            }
        }
    }
}
