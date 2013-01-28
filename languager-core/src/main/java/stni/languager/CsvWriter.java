package stni.languager;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 *
 */
public class CsvWriter implements Closeable {
    private final Writer out;
    private final char separator;
    private boolean start;

    public CsvWriter(Writer out, char separator) {
        this.out = out;
        this.separator = separator;
        start = true;
    }

    public void writeField(String value) throws IOException {
        writeSeparator();
        if (value == null) {
            return;
        }
        if (value.contains("\"") || value.indexOf(separator) >= 0 || value.contains("\n") || value.contains("\r")) {
            out.write("\"" + value.replace("\"", "\"\"") + "\"");
        } else {
            out.write(value);
        }
    }


    public void writeLine(List<String> values) throws IOException {
        for (String value : values) {
            writeField(value);
        }
        writeEndOfLine();
    }

    public void writeEndOfLine() throws IOException {
        out.write("\r\n");
        start = true;
    }

    public void close() throws IOException {
        out.close();
    }

    private void writeSeparator() throws IOException {
        if (start) {
            start = false;
        } else {
            out.write(separator);
        }
    }
}
