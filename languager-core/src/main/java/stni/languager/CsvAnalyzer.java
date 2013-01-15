package stni.languager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CsvAnalyzer {
    private final CsvReader reader;
    private final List<String> first;

    public CsvAnalyzer(File file, String encoding, char csvSeparator) throws IOException {
        this(new InputStreamReader(new FileInputStream(file), encoding), csvSeparator);
    }

    public CsvAnalyzer(Reader reader, char csvSeparator) throws IOException {
        this.reader = new CsvReader(reader, csvSeparator);
        first = this.reader.readLine();
    }

    public List<List<String>> compareDefaultValueWithLanguage(String lang) throws IOException {
        int index = getColumnOfLang(lang);
        List<List<String>> res = new ArrayList<List<String>>();
        while (!reader.isEndOfInput()) {
            final List<String> line = reader.readLine();
            if (line.size() > index) {
                final String entry = line.get(index);
                if (entry.length() > 0 && !getDefaultValue(line).equals(entry)) {
                    res.add(line);
                }
            }
        }
        return res;
    }

    private int getColumnOfLang(String lang) {
        int index = first.indexOf(lang);
        if (index < 0) {
            throw new IllegalArgumentException("No column with language '" + lang + "' found");
        }
        return index;
    }

    public String getKey(List<String> line) {
        return line.get(MessagesWriter.KEY_COLUMN);
    }

    public String getDefaultValue(List<String> line) {
        return line.get(MessagesWriter.DEFAULT_COLUMN);
    }

    public String getValue(List<String> line, String lang) {
        return line.get(getColumnOfLang(lang));
    }
}
