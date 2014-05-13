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

import java.io.IOException;
import java.util.*;

import static guru.nidi.languager.Message.Status.NOT_FOUND;
import static guru.nidi.languager.Message.Status.ofSymbol;

/**
 *
 */
public class MessageLine implements Iterable<String> {
    private static final int KEY_COLUMN = 0;
    private static final int STATUS_COLUMN = 1;
    private static final int OCCURRENCE_COLUMN = 2;
    private static final int DEFAULT_COLUMN = 3;
    private static final int FIRST_LANG_COLUMN = 4;
    private static final List<String> MINIMAL_FIRST_LINE = Arrays.asList("key", "status", "occurs", "default value");

    private final List<String> line;

    private MessageLine(List<String> line) {
        this.line = line;
    }

    public static MessageLine of(List<String> line) {
        return new MessageLine(line);
    }

    public MessageLine withValues(MessageLine first, Map<String, String> values) {
        List<String> newLine = new ArrayList<>(line);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            int index = entry.getKey().equals("default") ? 0 : first.findLang(entry.getKey());
            newLine.set(index + DEFAULT_COLUMN, entry.getValue());
        }
        return of(newLine);
    }

    public static MessageLine firstLine(String... languages) {
        ArrayList<String> res = new ArrayList<>(MINIMAL_FIRST_LINE);
        res.addAll(Arrays.asList(languages));
        return new MessageLine(res);
    }

    public Message.Status readStatus() {
        return line.get(STATUS_COLUMN).length() == 0 ? NOT_FOUND : ofSymbol(line.get(STATUS_COLUMN).charAt(0));
    }

    public String readKey() {
        return line.get(KEY_COLUMN);
    }

    public String readDefaultValue(String ifNotAvailable) {
        return line.size() > DEFAULT_COLUMN ? line.get(DEFAULT_COLUMN) : ifNotAvailable;
    }

    /**
     * @param language 0=default, 1=first lang, etc.
     * @return
     */
    public String readValue(int language, String ifNotAvailable) {
        return (line.size() > language + DEFAULT_COLUMN && line.get(language + DEFAULT_COLUMN).length() > 0)
                ? line.get(language + DEFAULT_COLUMN)
                : ifNotAvailable;
    }

    public int findLang(String lang) {
        int index = line.indexOf(lang);
        if (index < 0) {
            throw new IllegalArgumentException("No column with language '" + lang + "' found");
        }
        return index - DEFAULT_COLUMN;
    }

    public int languageCount() {
        return line.size() - DEFAULT_COLUMN;
    }


    public void checkFirstLine() {
        if (!isCorrectFirstLine()) {
            throw new RuntimeException("The first line of the CSV file must start with '" + MINIMAL_FIRST_LINE + "' but starts with '" + line + "'");
        }
    }

    public boolean isCorrectFirstLine() {
        if (line.size() < MINIMAL_FIRST_LINE.size()) {
            return false;
        }
        for (int i = 0; i < MINIMAL_FIRST_LINE.size(); i++) {
            if (!MINIMAL_FIRST_LINE.get(i).equalsIgnoreCase(line.get(i))) {
                return false;
            }
        }
        return true;
    }

    public void writeValues(Message msg, CsvWriter out) throws IOException {
        for (int i = FIRST_LANG_COLUMN; i < line.size(); i++) {
            out.writeField(msg.getValues().get(line.get(i)));
        }
    }

    public void readValuesIntoMessage(MessageLine languages, Message msg) {
        for (int i = MessageLine.FIRST_LANG_COLUMN; i < Math.min(languages.line.size(), line.size()); i++) {
            msg.addValue(languages.line.get(i), line.get(i));
        }
    }

    public boolean isEmpty() {
        return !(line.size() > 1 || line.get(0).trim().length() > 0);
    }

    public Map<String, String> asMap(MessageLine first) {
        Map<String, String> res = new LinkedHashMap<>();
        String defaultVal = readValue(0, "");
        res.put("default", defaultVal);
        for (int i = 1; i < first.languageCount(); i++) {
            res.put(first.readValue(i, ""), readValue(i, readDefaultValue("")));
        }
        return res;
    }

    public Iterator<String> iterator() {
        return line.iterator();
    }

    @Override
    public String toString() {
        return "MessageLine{" + line + '}';
    }
}
