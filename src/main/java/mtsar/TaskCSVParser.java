package mtsar;

import mtsar.api.Process;
import mtsar.api.Task;
import mtsar.api.sql.TaskDAO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Iterator;

public class TaskCSVParser {
    public static final CSVFormat FORMAT = CSVFormat.DEFAULT.
            withHeader("external_id", "type", "description", "answers", "datetime").
            withSkipHeaderRecord();

    public static class TaskIterator implements Iterator<Task> {
        private final Process process;
        private final Iterator<CSVRecord> records;

        public TaskIterator(Process process, Iterator<CSVRecord> records) {
            this.process = process;
            this.records = records;
        }

        @Override
        public boolean hasNext() {
            return records.hasNext();
        }

        @Override
        public Task next() {
            final CSVRecord row = records.next();
            final String type = row.get("type");
            final String description = row.get("description");
            final String[] answers = row.get("answers").split("\\|");
            final String externalId = row.get("external_id");
            final String datetime = row.get("datetime");
            System.out.println(row.toString());
            return Task.builder().
                    setProcess(process.getId()).
                    setType(type.isEmpty() ? null : type).
                    setDescription(description.isEmpty() ? null : description).
                    setAnswers(answers).
                    setExternalId(externalId.isEmpty() ? null : externalId).
                    setDateTime(new Timestamp(datetime.isEmpty() ? System.currentTimeMillis() : Long.valueOf(datetime) * 1000L)).
                    build();
        }
    }

    public static void insert(File file, Process process, TaskDAO dao) throws IOException {
        try (final CSVParser csv = CSVParser.parse(file, StandardCharsets.UTF_8, FORMAT)) {
            dao.insert(new TaskIterator(process, csv.iterator()));
        }
    }
}
