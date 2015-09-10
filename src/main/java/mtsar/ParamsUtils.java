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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public final class ParamsUtils {
    @Nonnull
    public static Map<String, List<String>> nested(MultivaluedMap<String, String> params, String prefix) {
        final Pattern pattern = Pattern.compile("^(" + Pattern.quote(prefix) + "\\[(\\w+?)\\])(\\[\\d+\\]|)$");

        final Map<String, Matcher> prefixes = params.keySet().stream().
                map(param -> pattern.matcher(param)).
                filter(Matcher::matches).
                collect(Collectors.toMap(matcher -> matcher.group(1), Function.identity(), (m1, m2) -> m1));

        final Map<String, List<String>> values = prefixes.entrySet().stream().
                collect(Collectors.toMap(entry -> entry.getValue().group(2), entry -> extract(params, entry.getKey())));

        return values;
    }

    @Nonnull
    public static List<String> extract(MultivaluedMap<String, String> params, String prefix) {
        final String regexp = "^" + Pattern.quote(prefix) + "(\\[\\d+\\]|)$";

        final List<String> values = params.entrySet().stream().
                filter(entries -> entries.getKey().matches(regexp) && entries.getValue() != null && !entries.getValue().isEmpty()).
                flatMap(entries -> entries.getValue().stream()).
                collect(Collectors.toList());

        return values;
    }

    public final static void validate(Validator validator, Object... objects) throws ConstraintViolationException {
        final Set<ConstraintViolation<Object>> violations = new HashSet<>();
        for (final Object object : objects) violations.addAll(validator.validate(object));
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);
    }
}
