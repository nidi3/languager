package stni.languager;

import java.util.*;

/**
 *
 */
public class Message {
    public interface Transformer {
        String transform(String lang, String value);
    }

    public enum Status {
        FOUND('+'), MANUAL('*'), NOT_FOUND('-');

        private final char symbol;

        private Status(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }

        public static Status ofSymbol(char symbol) {
            for (Status status : values()) {
                if (symbol == status.getSymbol()) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid symbol '" + symbol + "'");
        }
    }

    private final String key;
    private final Status status;
    private final String defaultValue;
    private final Map<String, String> values = new HashMap<String, String>();
    private final SortedSet<SourcePosition> occurrences = new TreeSet<SourcePosition>();

    public Message(String key, Status status, String defaultValue) {
        this.key = key;
        this.status = status;
        this.defaultValue = defaultValue;
    }

    public Message transformed(Transformer transformer) {
        Message res = new Message(getKey(), getStatus(), transformer.transform("", getDefaultValue()));
        for (Map.Entry<String, String> value : values.entrySet()) {
            res.addValue(value.getKey(), transformer.transform(value.getKey(), value.getValue()));
        }
        res.addOccurrences(getOccurrences());
        return res;
    }

    public String getKey() {
        return key;
    }

    public Status getStatus() {
        return status;
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

    public Set<SourcePosition> getOccurrences() {
        return occurrences;
    }

    public void addValue(String lang, String value) {
        values.put(lang, value);
    }

    public void addOccurrence(SourcePosition occurrence) {
        occurrences.add(occurrence);
    }

    public void addOccurrences(Collection<SourcePosition> occurrences) {
        this.occurrences.addAll(occurrences);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (status != message.status) return false;
        if (defaultValue != null ? !defaultValue.equals(message.defaultValue) : message.defaultValue != null)
            return false;
        if (!key.equals(message.key)) return false;
        if (!values.equals(message.values)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + (status.hashCode());
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + values.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "key='" + key + '\'' +
                ", status=" + status +
                ", defaultValue='" + defaultValue + '\'' +
                ", values=" + values +
                ", occurrences=" + occurrences +
                '}';
    }
}
