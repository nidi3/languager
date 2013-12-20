package stni.languager;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CsvReader implements Closeable {
    private static final char EOI = (char) -1;
    private final Reader in;
    private final char separator;
    private char c;

    public CsvReader(Reader in, char separator) throws IOException {
        this.in = in;
        this.separator = separator;
        read();
    }

    public CsvReader(String in, char separator) throws IOException {
        this(new StringReader(in), separator);
    }

    private void read() throws IOException {
        c = (char) in.read();
    }

    private String unescapeCsv(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }
        return value;
    }

    public MessageLine readMessageLine() throws IOException {
        return MessageLine.of(readLine());
    }

    public List<String> readLine() throws IOException {
        List<String> res = new ArrayList<String>();
        StringBuilder curr = new StringBuilder();
        boolean quote = false;
        f:
        while (c != EOI) {
            if (c == separator) {
                if (!quote) {
                    res.add(unescapeCsv(curr.toString()));
                    curr = new StringBuilder();
                } else {
                    curr.append(c);
                }
                read();
            } else {
                switch (c) {
                    case '\r':
                    case '\n':
                        if (!quote) {
                            do {
                                read();
                            } while ((c == '\r' || c == '\n') && c != EOI);
                            break f;
                        } else {
                            curr.append(c);
                            read();
                        }
                        break;
                    case '"':
                        do {
                            quote = !quote;
                            if (c != EOI) {
                                curr.append(c);
                            }
                            read();
                        } while (c == '"');
                        break;
                    default:
                        curr.append(c);
                        read();
                        break;
                }
            }
        }
        res.add(unescapeCsv(curr.toString()));
        return res;
    }

    public boolean isEndOfInput() {
        return c == EOI;
    }

    public void close() throws IOException {
        in.close();
    }
}
