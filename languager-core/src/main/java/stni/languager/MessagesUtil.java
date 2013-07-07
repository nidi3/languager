package stni.languager;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static stni.languager.Message.Status.NOT_FOUND;
import static stni.languager.Message.Status.ofSymbol;

/**
 *
 */
public class MessagesUtil {
    private MessagesUtil() {
    }

    static final int KEY_COLUMN = 0;
    static final int STATUS_COLUMN = 1;
    static final int DEFAULT_COLUMN = 2;
    static final int FIRST_LANG_COLUMN = 3;
    static final List<String> MINIMAL_FIRST_LINE = Arrays.asList("key", "status", "default value");

    static Message.Status statusOfLine(List<String> line) {
        return line.get(STATUS_COLUMN).length() == 0 ? NOT_FOUND : ofSymbol(line.get(STATUS_COLUMN).charAt(0));
    }
}
