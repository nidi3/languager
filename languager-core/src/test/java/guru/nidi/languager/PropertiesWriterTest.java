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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class PropertiesWriterTest extends BaseTest {
    private PropertiesWriter writer = new PropertiesWriter(',');
    private final File tempDir;

    public PropertiesWriterTest() throws IOException {
        tempDir = File.createTempFile("pre", "post").getParentFile();
    }

    @Test
    public void testWrite() throws Exception {
        File base = fromTestDir("");
        writer.write(Util.reader(new File(base, "existing.csv"), Util.ISO), "", tempDir, "testMsg");
        Properties prop = new Properties();
        prop.load(new FileInputStream(new File(tempDir, "testMsg_en.properties")));
        assertEquals(2, prop.size());
        assertNull(prop.getProperty("keyXXX"));
        assertEquals("value1", prop.getProperty("key3"));
        assertEquals("value3", prop.getProperty("key1"));
        prop.load(new FileInputStream(new File(tempDir, "testMsg_de.properties")));
        assertEquals(2, prop.size());
        assertEquals("wert3", prop.getProperty("key3"));
        assertEquals("blu", prop.getProperty("key1"));
    }

    @Test
    public void testNewlines() throws Exception {
        writer.write(new StringReader("key,status,occurs,default value,en,de\n" +
                "key1,+,,\"\n\n1\n\n\",,\n" +
                "key2,*,,a,,\n" +
                "key3,+,,\"\na\rb\r\nc\",,\n"), "", tempDir, "newlines");
        Properties prop = new Properties();
        prop.load(new FileInputStream(new File(tempDir, "newlines_en.properties")));
        assertEquals("1 ", prop.getProperty("key1"));
        assertEquals("a", prop.getProperty("key2"));
        assertEquals("a b c", prop.getProperty("key3"));
    }
}
