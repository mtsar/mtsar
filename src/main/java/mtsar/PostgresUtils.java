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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.jdbc2.AbstractJdbc2Array;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class PostgresUtils {
    public final static TypeReference<Map<String, String>> MAP_STRING_TO_STRING = new TypeReference<Map<String, String>>() {
    };

    public static String buildArrayString(@Nonnull Collection<String> elements) {
        checkNotNull(elements);
        return buildArrayString(elements.toArray(new String[elements.size()]));
    }

    public static String buildArrayString(@Nonnull String[] elements) {
        checkNotNull(elements);
        final StringBuilder sb = new StringBuilder("{");
        for (int i = 0, len = elements.length; i < len; i++) {
            if (i > 0) sb.append(',');
            final String element = elements[i];
            if (element != null) {
                AbstractJdbc2Array.escapeArrayElement(sb, element);
            } else {
                sb.append("NULL");
            }
        }
        return sb.append('}').toString();
    }

    public static String buildJSONString(@Nonnull Map<String, String> elements) {
        checkNotNull(elements);
        try {
            return new ObjectMapper().writeValueAsString(elements);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> parseJSONString(@Nonnull String json) {
        checkNotNull(json);
        try {
            return new ObjectMapper().readValue(json, MAP_STRING_TO_STRING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
