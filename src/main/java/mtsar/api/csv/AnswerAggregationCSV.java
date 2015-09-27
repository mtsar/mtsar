/*
 * Copyright 2015 Dmitry Ustalov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mtsar.api.csv;

import mtsar.api.AnswerAggregation;
import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Comparator;

public final class AnswerAggregationCSV {
    public static final CSVFormat FORMAT = CSVFormat.EXCEL.withHeader();
    static final String[] HEADER = {"task_id", "answers"};
    static final Comparator<AnswerAggregation> ORDER = (a1, a2) -> a1.getTask().getId().compareTo(a2.getTask().getId());

    public static void write(Collection<AnswerAggregation> aggregations, OutputStream output) throws IOException {
        try (final Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            final Iterable<String[]> iterable = () -> aggregations.stream().sorted(ORDER).map(aggregation -> new String[]{
                    Integer.toString(aggregation.getTask().getId()), // task_id
                    String.join("|", aggregation.getAnswers())       // answers
            }).iterator();

            FORMAT.withHeader(HEADER).print(writer).printRecords(iterable);
        }
    }
}
