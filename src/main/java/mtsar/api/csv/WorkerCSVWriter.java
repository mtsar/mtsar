package mtsar.api.csv;

import mtsar.api.Worker;
import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class WorkerCSVWriter {
    public static final String[] HEADER = {"id", "external_id", "process", "datetime"};

    public static void write(List<Worker> workers, OutputStream output) throws IOException {
        try (final Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            CSVFormat.DEFAULT.withHeader(HEADER).print(writer).printRecords(
                    workers.stream().map(worker -> new String[]{
                            Integer.toString(worker.getId()),
                            worker.getExternalId(),
                            worker.getProcess(),
                            Long.toString(worker.getDateTime().toInstant().getEpochSecond())
                    }).iterator()
            );
        }
    }
}
