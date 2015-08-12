package mtsar.api.csv;

import mtsar.api.Task;
import org.apache.commons.collections4.iterators.IteratorIterable;
import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class TaskCSVWriter {
    public static final String[] HEADER = {"id", "external_id", "type", "process", "description", "answers", "datetime"};

    public static void write(List<Task> tasks, OutputStream output) throws IOException {
        try (final Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            CSVFormat.DEFAULT.withHeader(HEADER).print(writer).printRecords(new IteratorIterable<>(
                    tasks.stream().map(task -> new String[]{
                            Integer.toString(task.getId()),
                            task.getExternalId(),
                            task.getType(),
                            task.getProcess(),
                            task.getDescription(),
                            String.join("|", task.getAnswers()),
                            Long.toString(task.getDateTime().toInstant().getEpochSecond())
                    }).iterator()
            ));
        }
    }
}
