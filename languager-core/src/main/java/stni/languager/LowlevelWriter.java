package stni.languager;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public interface LowlevelWriter extends Closeable {
    void writeField(String value) throws IOException;

    void writeLine(List<String> values) throws IOException;

    void newLine() throws IOException;
}
