package mtsar.api.csv;

import com.google.common.base.Charsets;
import mtsar.api.Answer;
import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

public final class AnswerCSVWriter {
    public static class AnswerIterator implements Iterator {
        private final Iterator<Answer> answers;

        public AnswerIterator(Iterable<Answer> answers) {
            this.answers = answers.iterator();
        }

        @Override
        public boolean hasNext() {
            return answers.hasNext();
        }

        @Override
        public Object[] next() {
            final Answer answer = answers.next();
            return new String[]{
                    Integer.toString(answer.getId()),
                    answer.getExternalId(),
                    answer.getProcess(),
                    Integer.toString(answer.getTaskId()),
                    Integer.toString(answer.getWorkerId()),
                    String.join("|", answer.getAnswers()),
                    Long.toString(answer.getDateTime().toInstant().getEpochSecond())};
        }
    }

    public static final String[] HEADER = {"id", "external_id", "process", "worker_id", "task_id", "answers", "datetime"};

    public static void write(List<Answer> answers, OutputStream output) throws IOException {
        try (final Writer writer = new OutputStreamWriter(output, Charsets.UTF_8)) {
            CSVFormat.DEFAULT.withHeader(HEADER).print(writer).printRecords(() -> new AnswerIterator(answers));
        }
    }
}
