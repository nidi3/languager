package stni.languager;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.*;

import static stni.languager.Message.Status.FOUND;

/**
 *
 */
public class PropertiesFinder {
    public static final String PROPERTIES = ".properties";
    private List<String> propertyLocations = new ArrayList<>();

    public void addPropertyLocation(String location) {
        propertyLocations.add(location);
    }

    public SortedMap<String, Message> findProperties() throws IOException {
        SortedMap<String, Message> messages = new TreeMap<>();
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
