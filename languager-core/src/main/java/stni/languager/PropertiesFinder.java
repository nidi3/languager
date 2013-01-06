package stni.languager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import static stni.languager.Message.Status.FOUND;

/**
 *
 */
public class PropertiesFinder {
    public static final String PROPERTIES = ".properties";
    private List<String> propertyLocations = new ArrayList<String>();

    public void addPropertyLocation(String location) {
        propertyLocations.add(location);
    }

    public SortedMap<String, Message> findProperties() throws IOException {
        SortedMap<String, Message> messages = new TreeMap<String, Message>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        for (String propertyLocation : propertyLocations) {
            Resource[] resources = resolver.getResources(propertyLocation + "*" + PROPERTIES);
            for (Resource resource : resources) {
                String lang = resource.getFilename();
                lang = lang.substring(Math.max(0, lang.length() - 20), lang.length() - PROPERTIES.length());
                int pos = lang.indexOf('_');
                if (pos < 0) {
                    lang = "";
                } else {
                    lang = lang.substring(pos + 1);
                }
                Properties props = new Properties();
                props.load(resource.getInputStream());
                for (String name : props.stringPropertyNames()) {
                    Message message = messages.get(name);
                    if (message == null) {
                        message = new Message(name, FOUND, null);
                        messages.put(name, message);
                    }
                    message.addValue(lang, props.getProperty(name));
                }
            }
        }
        return messages;
    }
}
