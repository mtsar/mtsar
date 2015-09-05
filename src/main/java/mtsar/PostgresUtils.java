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

import org.postgresql.jdbc2.AbstractJdbc2Array;

import javax.annotation.Nonnull;
import java.util.Collection;

public class PostgresUtils {
    public static String buildArrayString(@Nonnull Collection<String> elements) {
        return buildArrayString(elements.toArray(new String[elements.size()]));
    }

    public static String buildArrayString(@Nonnull String[] elements) {
        final StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (int i = 0, len = elements.length; i < len; i++) {
            if (i > 0) sb.append(',');
            final String element = elements[i];
            if (element != null) {
                AbstractJdbc2Array.escapeArrayElement(sb, element);
            } else {
                sb.append("NULL");
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
