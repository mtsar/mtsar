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
import mtsar.api.Task;
import mtsar.api.csv.TaskCSV;
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

public class TaskCSVTest {
    private static final Stage stage = mock(Stage.class);
    private static final Task task1 = fixture("task1.json", Task.class);
    private static final Task task2 = fixture("task2.json", Task.class);
    private static final Collection<Task> tasks = Arrays.asList(task1, task2);

    @Before
    public void setup() {
        when(stage.getId()).thenReturn("1");
    }

    @Test
    public void testCSV() throws IOException {
        try (final PipedInputStream pis = new PipedInputStream()) {
            try (final PipedOutputStream pos = new PipedOutputStream(pis)) {
                TaskCSV.write(tasks, pos);

                try (final Reader reader = new InputStreamReader(pis, StandardCharsets.UTF_8)) {
                    try (final CSVParser csv = new CSVParser(reader, TaskCSV.FORMAT)) {
                        final List<Task> parsed = Lists.newArrayList(TaskCSV.parse(stage, csv));
                        assertThat(parsed).hasSize(2);
                        assertThat(parsed).usingElementComparatorIgnoringFields("dateTime").isEqualTo(tasks);
                    }
                }
            }
        }
    }
}
