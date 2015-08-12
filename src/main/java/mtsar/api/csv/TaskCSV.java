package mtsar.api.csv;

import mtsar.api.Process;
import mtsar.api.Task;
import org.apache.commons.collections4.iterators.IteratorIterable;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

public final class TaskCSV {
    public static final CSVFormat FORMAT = CSVFormat.EXCEL.withHeader();

    public static Iterator<Task> parse(Process process, Iterator<CSVRecord> records) {
        final Iterable<CSVRecord> iterable = () -> records;
        return StreamSupport.stream(iterable.spliterator(), false).map(row -> {
            final String id = row.isSet("id") ? row.get("id") : null;
            final String[] tags = row.isSet("tags") && !StringUtils.isEmpty(row.get("tags")) ? row.get("tags").split("\\|") : null;
            final String type = row.get("type");
            final String description = row.isSet("description") ? row.get("description") : null;
            final String[] answers = row.isSet("answers") && !StringUtils.isEmpty(row.get("answers")) ? row.get("answers").split("\\|") : null;
            final String datetime = row.isSet("datetime") ? row.get("datetime") : null;

            return Task.builder().
                    setId(StringUtils.isEmpty(id) ? null : Integer.valueOf(id)).
                    setProcess(process.getId()).
                    setTags(tags).
                    setDateTime(new Timestamp(StringUtils.isEmpty(datetime) ? System.currentTimeMillis() : Long.valueOf(datetime) * 1000L)).
                    setType(StringUtils.defaultIfEmpty(type, null)).
                    setDescription(StringUtils.defaultIfEmpty(description, null)).
                    setAnswers(answers).
                    build();
        }).iterator();
    }

    public static final String[] HEADER = {"id", "process", "datetime", "tags", "type", "description", "answers"};

    public static void write(List<Task> tasks, OutputStream output) throws IOException {
        try (final Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            FORMAT.withHeader(HEADER).print(writer).printRecords(new IteratorIterable<>(
                    tasks.stream().map(task -> new String[]{
                            Integer.toString(task.getId()),                                 // id
                            task.getProcess(),                                              // process
                            Long.toString(task.getDateTime().toInstant().getEpochSecond()), // datetime
                            String.join("|", task.getTags()),                               // tags
                            task.getType(),                                                 // type
                            task.getDescription(),                                          // description
                            String.join("|", task.getAnswers()),                            // answers
                    }).iterator()
            ));
        }
    }
}
