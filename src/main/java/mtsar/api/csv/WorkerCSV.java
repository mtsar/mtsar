package mtsar.api.csv;

import mtsar.api.Process;
import mtsar.api.Worker;
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

public final class WorkerCSV {
    public static final String[] HEADER = {"id", "tags", "process", "datetime"};

    public static final CSVFormat FORMAT = CSVFormat.DEFAULT.withHeader(HEADER).withSkipHeaderRecord();

    public static Iterator<Worker> parse(Process process, Iterator<CSVRecord> records) {
        final Iterable<CSVRecord> iterable = () -> records;
        return StreamSupport.stream(iterable.spliterator(), false).map(row -> {
            final String id = row.isSet("id") ? row.get("id") : null;
            final String[] tags = row.isSet("tags") && !StringUtils.isEmpty(row.get("tags")) ? row.get("tags").split("\\|") : null;
            final String datetime = row.isSet("datetime") ? row.get("datetime") : null;

            return Worker.builder().
                    setId(Integer.valueOf(id)).
                    setProcess(process.getId()).
                    setTags(tags).
                    setDateTime(new Timestamp(StringUtils.isEmpty(datetime) ? System.currentTimeMillis() : Long.valueOf(datetime) * 1000L)).
                    build();
        }).iterator();
    }

    public static void write(List<Worker> workers, OutputStream output) throws IOException {
        try (final Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            CSVFormat.DEFAULT.withHeader(HEADER).print(writer).printRecords(new IteratorIterable<>(
                    workers.stream().map(worker -> new String[]{
                            Integer.toString(worker.getId()),
                            String.join("|", worker.getTags()),
                            worker.getProcess(),
                            Long.toString(worker.getDateTime().toInstant().getEpochSecond())
                    }).iterator()
            ));
        }
    }
}