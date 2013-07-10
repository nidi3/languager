package stni.languager;

import java.io.IOException;
import java.util.List;

/**
 *
 */
public abstract class AbstractLowlevelWriter implements LowlevelWriter {
    public void writeLine(List<String> values) throws IOException {
        for (String value : values) {
            writeField(value);
        }
        newLine();
    }
}
