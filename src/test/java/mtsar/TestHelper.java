package mtsar;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.FixtureHelpers;

import java.io.IOException;

public final class TestHelper {
    private static final ObjectMapper JSON = Jackson.newObjectMapper();

    public static <T> T fixture(String filename, Class<T> valueType) {
        try {
            return JSON.readValue(FixtureHelpers.fixture("fixtures/" + filename), valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
