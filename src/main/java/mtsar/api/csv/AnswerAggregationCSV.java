package mtsar.api.csv;

import mtsar.api.AnswerAggregation;
import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public final class AnswerAggregationCSV {
    public static final CSVFormat FORMAT = CSVFormat.EXCEL.withHeader();

    public static final String[] HEADER = {"process", "task_id", "answers"};

    public static void write(Collection<AnswerAggregation> aggregations, OutputStream output) throws IOException {
        try (final Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            final Iterable<String[]> iterable = () -> aggregations.stream().map(aggregation -> new String[]{
                    aggregation.getTask().getProcess(),              // process
                    Integer.toString(aggregation.getTask().getId()), // task_id
                    String.join("|", aggregation.getAnswers())       // answers
            }).iterator();

            FORMAT.withHeader(HEADER).print(writer).printRecords(iterable);
        }
    }
}
