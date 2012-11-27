package stni.languager;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Message {
    public interface Transformer {
        String transform(String lang, String value);
    }

    private final String key;
    private final boolean known;
    private final String defaultValue;
    private final Map<String, String> values = new HashMap<String, String>();

    public Message(String key, boolean known, String defaultValue) {
        this.key = key;
        this.known = known;
        this.defaultValue = defaultValue;
    }

    public Message transformed(Transformer transformer) {
        Message res = new Message(getKey(), isKnown(), transformer.transform("", getDefaultValue()));
        for (Map.Entry<String, String> value : values.entrySet()) {
            res.addValue(value.getKey(), transformer.transform(value.getKey(), value.getValue()));
        }
        return res;
    }

    public String getKey() {
        return key;
    }

    public boolean isKnown() {
        return known;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public String getDefaultValueOrLang() {
        return getDefaultValue() != null ? getDefaultValue() : getValues().get("");
    }

    public void addValue(String lang, String value) {
        values.put(lang, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (known != message.known) return false;
        if (defaultValue != null ? !defaultValue.equals(message.defaultValue) : message.defaultValue != null)
            return false;
        if (!key.equals(message.key)) return false;
        if (!values.equals(message.values)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + (known ? 1 : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + values.hashCode();
        return result;
    }
}
