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
 *
 */
public class PropertiesWriterTest extends BaseTest {
    private PropertiesWriter writer = new PropertiesWriter(',');
    private File tempDir = File.createTempFile("pre", "post").getParentFile();

    public PropertiesWriterTest() throws IOException {
    }

    @Test
    public void testWrite() throws Exception {
        File base = fromTestDir("");
        writer.write(Util.reader(new File(base, "existing.csv"), Util.ISO), tempDir, "testMsg");
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
        writer.write(new StringReader("key,status,default value,en,de\n" +
                "key1,+,\"\n\n1\n\n\",,\n" +
                "key2,*,a,,\n" +
                "key3,+,\"\na\rb\r\nc\",,\n"), tempDir, "newlines");
        Properties prop = new Properties();
        prop.load(new FileInputStream(new File(tempDir, "newlines_en.properties")));
        assertEquals("1 ", prop.getProperty("key1"));
        assertEquals("a", prop.getProperty("key2"));
        assertEquals("a b c", prop.getProperty("key3"));
    }
}
