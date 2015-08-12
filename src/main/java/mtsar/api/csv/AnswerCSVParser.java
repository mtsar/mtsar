package mtsar.api.csv;

import mtsar.api.Answer;
import mtsar.api.Process;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.stream.StreamSupport;

public final class AnswerCSVParser {
    public static final CSVFormat FORMAT = CSVFormat.DEFAULT.
            withHeader("id", "tags", "process", "task_id", "worker_id", "answers", "datetime").
            withSkipHeaderRecord();

    public static Iterator<Answer> parse(Process process, Iterator<CSVRecord> records) {
        final Iterable<CSVRecord> iterable = () -> records;
        return StreamSupport.stream(iterable.spliterator(), false).map(row -> {
            final String id = row.isSet("id") ? row.get("id") : null;
            final String[] tags = row.isSet("tags") && !StringUtils.isEmpty(row.get("tags")) ? row.get("tags").split("\\|") : null;
            final String workerId = row.get("worker_id");
            final String taskId = row.get("task_id");
            final String[] answers = row.isSet("answers") && !StringUtils.isEmpty(row.get("answers")) ? row.get("answers").split("\\|") : null;
            final String datetime = row.isSet("datetime") ? row.get("datetime") : null;

            return Answer.builder().
                    setId(Integer.valueOf(id)).
                    setProcess(process.getId()).
                    setTags(tags).
                    setDateTime(new Timestamp(StringUtils.isEmpty(datetime) ? System.currentTimeMillis() : Long.valueOf(datetime) * 1000L)).
                    setWorkerId(Integer.valueOf(workerId)).
                    setTaskId(Integer.valueOf(taskId)).
                    setAnswers(answers).
                    build();
        }).iterator();
    }
}
