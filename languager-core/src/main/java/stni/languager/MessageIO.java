package stni.languager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static stni.languager.Message.Status.NOT_FOUND;
import static stni.languager.Message.Status.ofSymbol;

/**
 *
 */
public class MessageIO {
    //private List<String> line;

    private MessageIO() {
    }

    private static final int KEY_COLUMN = 0;
    private static final int STATUS_COLUMN = 1;
    private static final int OCCURRENCE_COLUMN = 2;
    private static final int DEFAULT_COLUMN = 3;
    private static final int FIRST_LANG_COLUMN = 4;
    private static final List<String> MINIMAL_FIRST_LINE = Arrays.asList("key", "status", "occurs", "default value");

    static Message.Status readStatus(List<String> line) {
        return line.get(STATUS_COLUMN).length() == 0 ? NOT_FOUND : ofSymbol(line.get(STATUS_COLUMN).charAt(0));
    }

  public  static String readKey(List<String> line) {
        return line.get(KEY_COLUMN);
    }

  public  static String readDefaultValue(List<String> line, String ifNotAvailable) {
        return line.size() > DEFAULT_COLUMN ? line.get(DEFAULT_COLUMN) : ifNotAvailable;
    }

    /**
     * @param line
     * @param language 0=default, 1=first lang, etc.
     * @return
     */
    static String readValue(List<String> line, int language, String ifNotAvailable) {
        return (line.size() > language + DEFAULT_COLUMN && line.get(language + DEFAULT_COLUMN).length() > 0)
                ? line.get(language + DEFAULT_COLUMN)
                : ifNotAvailable;
    }

    static int languageCount(List<String> line) {
        return line.size() - DEFAULT_COLUMN;
    }

    static List<String> firstLine(String... languages) {
        ArrayList<String> res = new ArrayList<String>(MINIMAL_FIRST_LINE);
        res.addAll(Arrays.asList(languages));
        return res;
    }

    static void checkFirstLine(List<String> line) {
        if (!isCorrectFirstLine(line)) {
            throw new RuntimeException("The first line of the CSV file must start with '" + MINIMAL_FIRST_LINE + "' but starts with '" + line + "'");
        }
    }

    static boolean isCorrectFirstLine(List<String> line) {
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

    static void writeValues(List<String> line, Message msg, CsvWriter out) throws IOException {
        for (int i = FIRST_LANG_COLUMN; i < line.size(); i++) {
            out.writeField(msg.getValues().get(line.get(i)));
        }
    }

    static void readValuesIntoMessage(List<String>line, List<String> languages, Message msg) throws IOException {
        for (int i = MessageIO.FIRST_LANG_COLUMN; i < Math.min(languages.size(), line.size()); i++) {
            msg.addValue(languages.get(i), line.get(i));
        }
    }
}
