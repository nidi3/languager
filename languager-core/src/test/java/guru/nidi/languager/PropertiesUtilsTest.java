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

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class PropertiesUtilsTest {
    @Test
    public void testEscapeSingleQuotes() throws Exception {
        assertEquals("a''b''c{0}d''e", PropertiesUtils.escapeSingleQuotes("a'b''c{0}d'e", false));
        assertEquals("a''b''c{0}", PropertiesUtils.escapeSingleQuotes("a'b''c{0}", true));
        assertEquals("a'b''c", PropertiesUtils.escapeSingleQuotes("a'b''c", false));
        assertEquals("a''b''c", PropertiesUtils.escapeSingleQuotes("a'b''c", true));
    }

    @Test
    public void testFindFirstSingleQuote() throws Exception {
        assertEquals(1, PropertiesUtils.findFirstSingleQuote("a'b''c{0}d'e", false));
        assertEquals(1, PropertiesUtils.findFirstSingleQuote("a'b''c{0}", true));
        assertEquals(-1, PropertiesUtils.findFirstSingleQuote("a'b''c", false));
        assertEquals(1, PropertiesUtils.findFirstSingleQuote("a'b''c", true));
    }
}
