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

package mtsar;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.FixtureHelpers;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public final class TestHelper {
    public static final TypeReference<Map<String, Object>> MAP_STRING_TO_OBJECT = new TypeReference<Map<String, Object>>() {
    };

    private static final ObjectMapper JSON = Jackson.newObjectMapper();

    public static <T> T fixture(String filename, Class<T> valueType) {
        try {
            return JSON.readValue(FixtureHelpers.fixture("fixtures/" + filename), valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> MultivaluedMap<String, String> params(@Nonnull T object) {
        final MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        final Map<String, Object> attributes = JSON.convertValue(object, MAP_STRING_TO_OBJECT);
        for (final Map.Entry<String, Object> attribute : attributes.entrySet()) {
            if (attribute.getValue() instanceof Collection) {
                for (final Object item : (Collection)attribute.getValue()) {
                    params.add(attribute.getKey(), item.toString());
                }
            } else {
                params.add(attribute.getKey(), attribute.getValue().toString());
            }
        }
        params.remove("id");
        params.addAll("datetime", params.remove("dateTime"));
        return params;
    }
}
