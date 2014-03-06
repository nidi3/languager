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
