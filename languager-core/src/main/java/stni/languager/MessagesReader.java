package stni.languager;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 *
 */
public class MessagesReader implements Closeable {
    private final CsvReader in;
    private final List<String> firstParts;

    public MessagesReader(File f, String encoding, char csvSeparator) throws IOException {
        this(Util.reader(f, encoding), csvSeparator);
    }

    public MessagesReader(Reader reader, char csvSeparator) throws IOException {
        this.in = new CsvReader(reader, csvSeparator);
        firstParts = toLowerCase(in.readLine());
        MessageIO.checkFirstLine(firstParts);
    }

    private List<String> toLowerCase(List<String> strings) {
        for (int i = 0; i < strings.size(); i++) {
            strings.set(i, strings.get(i).toLowerCase());
        }
        return strings;
    }


    public List<String> readLine() throws IOException {
        return in.readLine();
    }

    public boolean isEndOfInput() {
        return in.isEndOfInput();
    }

    public List<String> getFirstParts() {
        return firstParts;
    }

    public void close() throws IOException {
        in.close();
    }
}
