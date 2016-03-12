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

package mtsar.api.csv;

import com.google.common.collect.Sets;
import mtsar.api.Stage;
import mtsar.api.Task;
import mtsar.api.sql.TaskDAO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkArgument;

public final class TaskCSV {
    public static final CSVFormat FORMAT = CSVFormat.EXCEL.withHeader();
    static final String[] HEADER = {"id", "stage", "datetime", "tags", "type", "description", "answers"};
    static final Comparator<Task> ORDER = (t1, t2) -> t1.getId().compareTo(t2.getId());

    public static Iterator<Task> parse(Stage stage, CSVParser csv) {
        final Set<String> header = csv.getHeaderMap().keySet();
        checkArgument(!Sets.intersection(header, Sets.newHashSet(HEADER)).isEmpty(), "Unknown CSV header: %s", String.join(",", header));

        final Iterable<CSVRecord> iterable = csv::iterator;

        return StreamSupport.stream(iterable.spliterator(), false).map(row -> {
            final String id = row.isSet("id") ? row.get("id") : null;
            final String[] tags = row.isSet("tags") && !StringUtils.isEmpty(row.get("tags")) ? row.get("tags").split("\\|") : new String[0];
            final String type = row.get("type");
            final String description = row.isSet("description") ? row.get("description") : null;
            final String[] answers = row.isSet("answers") && !StringUtils.isEmpty(row.get("answers")) ? row.get("answers").split("\\|") : new String[0];
            final String datetime = row.isSet("datetime") ? row.get("datetime") : null;

            return new Task.Builder().
                    setId(StringUtils.isEmpty(id) ? null : Integer.valueOf(id)).
                    setStage(stage.getId()).
                    addAllTags(Arrays.asList(tags)).
                    setDateTime(new Timestamp(StringUtils.isEmpty(datetime) ? System.currentTimeMillis() : Long.parseLong(datetime) * 1000L)).
                    setType(StringUtils.defaultIfEmpty(type, TaskDAO.TASK_TYPE_SINGLE)).
                    setDescription(description).
                    addAllAnswers(Arrays.asList(answers)).
                    build();
        }).iterator();
    }

    public static void write(Collection<Task> tasks, OutputStream output) throws IOException {
        try (final Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            final Iterable<String[]> iterable = () -> tasks.stream().sorted(ORDER).map(task -> new String[]{
                    Integer.toString(task.getId()),                                 // id
                    task.getStage(),                                                // stage
                    Long.toString(task.getDateTime().toInstant().getEpochSecond()), // datetime
                    String.join("|", task.getTags()),                               // tags
                    task.getType(),                                                 // type
                    task.getDescription(),                                          // description
                    String.join("|", task.getAnswers()),                            // answers
            }).iterator();

            FORMAT.withHeader(HEADER).print(writer).printRecords(iterable);
        }
    }
}
