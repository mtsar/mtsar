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

import mtsar.util.PostgresUtils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostgresUtilsTest {
    @Test
    public void testEmpty() {
        assertThat(PostgresUtils.buildArrayString(new String[]{})).isEqualTo("{}");
    }

    @Test
    public void testOneElement() {
        assertThat(PostgresUtils.buildArrayString(new String[]{"foo"})).isEqualTo("{\"foo\"}");
    }

    @Test
    public void testManyElements() {
        assertThat(PostgresUtils.buildArrayString(new String[]{"foo", "b\"a\"r", "b\'a\'z"})).isEqualTo("{\"foo\",\"b\\\"a\\\"r\",\"b'a'z\"}");
    }
}
