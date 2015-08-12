package mtsar.api.csv;

import mtsar.api.Answer;
import org.apache.commons.collections4.iterators.IteratorIterable;
import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class AnswerCSVWriter {
    public static final String[] HEADER = {"id", "tags", "process", "task_id", "worker_id", "answers", "datetime"};

    public static void write(List<Answer> answers, OutputStream output) throws IOException {
        try (final Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            CSVFormat.DEFAULT.withHeader(HEADER).print(writer).printRecords(new IteratorIterable<>(
                    answers.stream().map(answer -> new String[]{
                            Integer.toString(answer.getId()),
                            String.join("|", answer.getTags()),
                            answer.getProcess(),
                            Integer.toString(answer.getTaskId()),
                            Integer.toString(answer.getWorkerId()),
                            String.join("|", answer.getAnswers()),
                            Long.toString(answer.getDateTime().toInstant().getEpochSecond())
                    }).iterator()
            ));
        }
    }
}
