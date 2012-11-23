package stni.languager;

import static org.junit.Assert.assertTrue;

import java.util.SortedMap;

import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: nidi
 * Date: 10.03.12
 * Time: 02:21
 * To change this template use File | Settings | File Templates.
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
