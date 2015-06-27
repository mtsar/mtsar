package mtsar.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface AnswerAggregation {
    static AnswerAggregation create(Task task, Answer answer) {
        return new AnswerAggregation() {
            @Override
            public Task getTask() {
                return task;
            }

            @Override
            public Answer getAnswer() {
                return answer;
            }
        };
    }

    @JsonProperty
    Task getTask();

    @JsonProperty
    Answer getAnswer();
}
