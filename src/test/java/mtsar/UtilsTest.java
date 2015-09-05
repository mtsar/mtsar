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

import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {
    @Test
    public void testParamsUtilsSingle() {
        final MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle("foo", "bar");
        final List<String> values = ParamsUtils.extract(params, "foo");
        assertThat(values).hasSize(1);
        assertThat(values.contains("bar")).isTrue();
    }

    @Test
    public void testParamsUtilsMultiple() {
        final MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle("foo[0]", "bar");
        params.putSingle("foo[1]", "baz");
        final List<String> values = ParamsUtils.extract(params, "foo");
        assertThat(values).hasSize(2);
        assertThat(values.contains("bar")).isTrue();
        assertThat(values.contains("baz")).isTrue();
    }

    @Test
    public void testParamsUtilsEmpty() {
        final MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle("f00", "loh");
        final List<String> values = ParamsUtils.extract(params, "foo");
        assertThat(values).isEmpty();
    }
}
