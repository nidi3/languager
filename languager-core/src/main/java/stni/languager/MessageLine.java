package stni.languager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static stni.languager.Message.Status.NOT_FOUND;
import static stni.languager.Message.Status.ofSymbol;

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

    public Iterator<String> iterator() {
        return line.iterator();
    }

}
