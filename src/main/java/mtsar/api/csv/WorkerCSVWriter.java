package mtsar.api.csv;

import com.google.common.base.Charsets;
import mtsar.api.Worker;
import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

public class WorkerCSVWriter {
    public static class WorkerIterator implements Iterator {
        private final Iterator<Worker> workers;

        public WorkerIterator(Iterable<Worker> workers) {
            this.workers = workers.iterator();
        }

        @Override
        public boolean hasNext() {
            return workers.hasNext();
        }

        @Override
        public Object[] next() {
            final Worker worker = workers.next();
            return new String[]{
                    Integer.toString(worker.getId()),
                    worker.getExternalId(),
                    worker.getProcess(),
                    Long.toString(worker.getDateTime().toInstant().getEpochSecond())};
        }
    }

    public static final String[] HEADER = {"id", "external_id", "process", "datetime"};

    public static void write(List<Worker> workers, OutputStream output) throws IOException {
        try (final Writer writer = new OutputStreamWriter(output, Charsets.UTF_8)) {
            CSVFormat.DEFAULT.withHeader(HEADER).print(writer).printRecords(() -> new WorkerIterator(workers));
        }
    }
}
