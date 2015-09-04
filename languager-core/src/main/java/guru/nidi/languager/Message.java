/*
 * Copyright (C) 2014 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.languager;

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

        Status(char symbol) {
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
    private final Map<String, String> values = new HashMap<>();
    private final SortedSet<SourcePosition> occurrences = new TreeSet<>();

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
