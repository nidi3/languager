/*
 * Copyright (C) 2014 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.languager;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class CsvReaderTest {
    @Test
    public void simple() throws IOException {
        assertEquals(Arrays.asList("a", "b", "c"), new CsvReader("a,b,c", ',').readLine());
        assertEquals(Arrays.asList("", "b", ""), new CsvReader(";b;", ';').readLine());
        assertEquals(Arrays.asList("a,b"), new CsvReader("\"a,b\"", ',').readLine());
        assertEquals(Arrays.asList("a\"b"), new CsvReader("\"a\"\"b\"", ',').readLine());
        assertEquals(Arrays.asList("a\"inner\"", "b"), new CsvReader("\"a\"\"inner\"\"\",b", ',').readLine());
        assertEquals(Arrays.asList("a", "a\nb", "c"), new CsvReader(new StringReader("a,\"a\nb\",c"), ',').readLine());
    }
}
