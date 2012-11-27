package stni.languager;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

import org.junit.Test;

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
