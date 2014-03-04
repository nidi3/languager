package guru.nidi.languager;

import static org.junit.Assert.assertTrue;

import java.util.SortedMap;

import org.junit.Test;

/**
 *
 */
public class PropertiesFinderTest {
    @Test
    public void testFindProperties() throws Exception {
        PropertiesFinder finder = new PropertiesFinder();
        finder.addPropertyLocation("classpath*:org/hibernate/validator/ValidationMessages");
        SortedMap<String, Message> properties = finder.findProperties();
        assertTrue(properties.size() > 10);
        Message message = properties.values().iterator().next();
        assertTrue(message.getValues().size() > 1);
    }
}
