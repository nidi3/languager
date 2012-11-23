package stni.languager;

import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: stni
 * Date: 09.03.12
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
public class MessagesWriter {
    static final int KEY_COLUMN = 0;
    static final int KNOWN_COLUMN = 1;
    static final int DEFAULT_COLUMN = 2;
    static final int FIRST_LANG_COLUMN = 3;

    private static final Comparator<List<String>> FIRST_ELEMENT_COMPARATOR = new Comparator<List<String>>() {

        public int compare(List<String> a, List<String> b) {
            if (a.isEmpty()) {
                return b.isEmpty() ? 0 : 1;
            }
            if (b.isEmpty()) {
                return -1;
            }
            return a.get(0).compareTo(b.get(0));
        }
    };

    private final String encoding;
    private final char csvSeparator;
    private final String firstLineStart;

    public MessagesWriter(String encoding, char csvSeparator) {
        this.encoding = encoding;
        this.csvSeparator = csvSeparator;
        firstLineStart = join("key", "unknown", "default value");
    }

    private String join(String... parts) {
        return StringUtils.arrayToDelimitedString(parts, "" + csvSeparator);
    }

    public void write(File f, List<Message> msgs) throws IOException {
        SortedMap<String, Message> entries = new TreeMap<String, Message>();
        for (Message msg : msgs) {
            entries.put(msg.getKey(), msg);
        }
        write(f, entries);
    }

    public void write(File f, SortedMap<String, Message> msgs) throws IOException {
        String first = firstLineStart + csvSeparator + join("en", "de");
        List<String> firstParts = new CsvReader(first, csvSeparator).readLine();
        if (f.exists()) {
            BufferedReader in = null;
            try {
                in = Util.reader(f, encoding);
                if (in.ready()) {
                    first = in.readLine();
                    if (!first.startsWith(firstLineStart)) {
                        throw new RuntimeException("The first line of the CSV file must start with '" + firstLineStart + "'");
                    }
                    firstParts = new CsvReader(first.toLowerCase(), csvSeparator).readLine();
                    CsvReader reader = new CsvReader(in, csvSeparator);
                    while (in.ready()) {
                        List<String> line = reader.readLine();
                        if (line.size() > 1 || line.get(0).trim().length() > 0) {
                            String key = line.get(KEY_COLUMN);
                            String defaultValue = line.size() > DEFAULT_COLUMN ? line.get(DEFAULT_COLUMN) : null;
                            Message entry = msgs.get(key);
                            if (entry == null) {
                                entry = new Message(key, false, defaultValue);
                                msgs.put(key, entry);
                            } else if (entry.getDefaultValue() == null) {
                                entry = new Message(key, true, defaultValue);
                                msgs.put(key, entry);
                            }
                            for (int i = FIRST_LANG_COLUMN; i < line.size(); i++) {
                                entry.addValue(firstParts.get(i), line.get(i));
                            }
                        }
                    }
                }
            } finally {
                Util.closeSilently(in);
            }
        }

        CsvWriter out = null;
        try {
            out = new CsvWriter(Util.writer(f, encoding), csvSeparator);
            out.writeLine(firstParts);
            writeLine(out, firstParts, msgs.values());
        } finally {
            out.close();
        }
    }

    private void writeLine(CsvWriter out, List<String> langs, Collection<Message> msgs) throws IOException {
        for (Message msg : msgs) {
            out.writeField(msg.getKey());
            out.writeField(msg.isKnown() ? "" : "*");
            out.writeField(msg.getDefaultValueOrLang());
            for (int i = FIRST_LANG_COLUMN; i < langs.size(); i++) {
                out.writeField(msg.getValues().get(langs.get(i)));
            }
            out.writeEndOfLine();
        }
    }
}
