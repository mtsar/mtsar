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
import mtsar.api.Answer;
import mtsar.api.Stage;
import mtsar.api.sql.AnswerDAO;
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

public final class AnswerCSV {
    public static final CSVFormat FORMAT = CSVFormat.EXCEL.withHeader();
    static final String[] HEADER = {"id", "stage", "datetime", "tags", "type", "task_id", "worker_id", "answers"};
    static final Comparator<Answer> TASK_ID_ORDER = (a1, a2) -> a1.getTaskId().compareTo(a2.getTaskId());
    static final Comparator<Answer> ORDER = TASK_ID_ORDER.thenComparing((a1, a2) -> a1.getId().compareTo(a2.getId()));

    public static Iterator<Answer> parse(Stage stage, CSVParser csv) {
        final Set<String> header = csv.getHeaderMap().keySet();
        checkArgument(!Sets.intersection(header, Sets.newHashSet(HEADER)).isEmpty(), "Unknown CSV header: %s", String.join(",", header));

        final Iterable<CSVRecord> iterable = csv::iterator;

        return StreamSupport.stream(iterable.spliterator(), false).map(row -> {
            final String id = row.isSet("id") ? row.get("id") : null;
            final String[] tags = row.isSet("tags") && !StringUtils.isEmpty(row.get("tags")) ? row.get("tags").split("\\|") : new String[0];
            final String type = row.isSet("type") ? row.get("type") : null;
            final String workerId = row.get("worker_id");
            final String taskId = row.get("task_id");
            final String[] answers = row.isSet("answers") && !StringUtils.isEmpty(row.get("answers")) ? row.get("answers").split("\\|") : new String[0];
            final String datetime = row.isSet("datetime") ? row.get("datetime") : null;

            return new Answer.Builder().
                    setId(StringUtils.isEmpty(id) ? null : Integer.valueOf(id)).
                    setStage(stage.getId()).
                    addAllTags(Arrays.asList(tags)).
                    setDateTime(new Timestamp(StringUtils.isEmpty(datetime) ? System.currentTimeMillis() : Long.parseLong(datetime) * 1000L)).
                    setType(StringUtils.defaultIfEmpty(type, AnswerDAO.ANSWER_TYPE_DEFAULT)).
                    setWorkerId(Integer.valueOf(workerId)).
                    setTaskId(Integer.valueOf(taskId)).
                    addAllAnswers(Arrays.asList(answers)).
                    build();
        }).iterator();
    }

    public static void write(Collection<Answer> answers, OutputStream output) throws IOException {
        try (final Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            final Iterable<String[]> iterable = () -> answers.stream().sorted(ORDER).map(answer -> new String[]{
                    Integer.toString(answer.getId()),                                 // id
                    answer.getStage(),                                                // stage
                    Long.toString(answer.getDateTime().toInstant().getEpochSecond()), // datetime
                    String.join("|", answer.getTags()),                               // tags
                    answer.getType(),                                                 // type
                    Integer.toString(answer.getTaskId()),                             // task_id
                    Integer.toString(answer.getWorkerId()),                           // worker_id
                    String.join("|", answer.getAnswers())                             // answers
            }).iterator();

            FORMAT.withHeader(HEADER).print(writer).printRecords(iterable);
        }
    }
}
