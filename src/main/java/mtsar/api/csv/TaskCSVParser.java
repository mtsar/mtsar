package mtsar.api.csv;

import mtsar.api.Process;
import mtsar.api.Task;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.stream.StreamSupport;

public final class TaskCSVParser {
    public static final CSVFormat FORMAT = CSVFormat.DEFAULT.
            withHeader("external_id", "type", "description", "answers", "datetime").
            withSkipHeaderRecord();

    public static Iterator<Task> parse(Process process, Iterator<CSVRecord> records) {
        final Iterable<CSVRecord> iterable = () -> records;
        return StreamSupport.stream(iterable.spliterator(), false).map(row -> {
            final String externalId = row.isSet("external_id") ? row.get("external_id") : null;
            final String type = row.get("type");
            final String description = row.isSet("description") ? row.get("description") : null;
            final String[] answers = row.get("answers").split("\\|");
            final String datetime = row.isSet("datetime") ? row.get("datetime") : null;

            return Task.builder().
                    setProcess(process.getId()).
                    setType(StringUtils.defaultIfEmpty(type, null)).
                    setDescription(StringUtils.defaultIfEmpty(description, null)).
                    setAnswers(answers).
                    setExternalId(StringUtils.defaultIfEmpty(externalId, null)).
                    setDateTime(new Timestamp(StringUtils.isEmpty(datetime) ? System.currentTimeMillis() : Long.valueOf(datetime) * 1000L)).
                    build();
        }).iterator();
    }
}
