package stni.languager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: nidi
 * Date: 09.03.12
 * Time: 23:07
 * To change this template use File | Settings | File Templates.
 */
public class PropertiesWriterTest {
    private PropertiesWriter writer = new PropertiesWriter(',');
    private File tempDir = File.createTempFile("pre", "post").getParentFile();

    public PropertiesWriterTest() throws IOException {
    }

    @Test
    public void testWrite() throws Exception {
        File base = new File("src/test/resources/stni/languager");
        writer.write(Util.reader(new File(base, "existing.csv"), Util.ISO), tempDir, "testMsg");
        Properties prop = new Properties();
        prop.load(new FileInputStream(new File(tempDir, "testMsg_en.properties")));
        assertEquals(2, prop.size());
        assertNull(prop.getProperty("key1"));
        assertEquals("value1", prop.getProperty("keyXXX"));
        assertEquals("value3", prop.getProperty("key3"));
        prop.load(new FileInputStream(new File(tempDir, "testMsg_de.properties")));
        assertEquals(2, prop.size());
        assertEquals("wert1", prop.getProperty("keyXXX"));
        assertEquals("blu", prop.getProperty("key3"));
    }

    @Test
    public void testNewlines() throws Exception {
        writer.write(new StringReader("key,unknown,default value,en,de\n" +
                "key1,,\"\n\n1\n\n\",,\n" +
                "key2,,a,,\n" +
                "key3,,\"\na\rb\r\nc\",,\n"), tempDir, "newlines");
        Properties prop = new Properties();
        prop.load(new FileInputStream(new File(tempDir, "newlines_en.properties")));
        assertEquals("1 ", prop.getProperty("key1"));
        assertEquals("a", prop.getProperty("key2"));
        assertEquals("a b c", prop.getProperty("key3"));
    }
}
