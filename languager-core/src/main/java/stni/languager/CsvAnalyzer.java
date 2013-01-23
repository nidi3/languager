package stni.languager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CsvAnalyzer {
    private final File file;
    private final CsvReader reader;
    private final List<String> first;

    public CsvAnalyzer(File file, String encoding, char csvSeparator) throws IOException {
        this.file = file;
        reader = new CsvReader(new InputStreamReader(new FileInputStream(file), encoding), csvSeparator);
        first = this.reader.readLine();
    }

    public List<FindResult> compareDefaultValueWithLanguage(String lang) throws IOException {
        int index = getColumnOfLang(lang);
        List<FindResult> res = new ArrayList<FindResult>();
        int lineNum = 1;
        while (!reader.isEndOfInput()) {
            final List<String> line = reader.readLine();
            lineNum++;
            if (line.size() > index) {
                final String entry = line.get(index);
                if (entry.length() > 0 && !getDefaultValue(line).equals(entry)) {
                    res.add(new FindResult(new SourcePosition(file, 0, 0, lineNum, 1), line));
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

    public String getKey(FindResult result) {
        return result.getFindings().get(MessagesWriter.KEY_COLUMN);
    }

    private String getDefaultValue(List<String> line) {
        return line.get(MessagesWriter.DEFAULT_COLUMN);
    }

    public String getDefaultValue(FindResult result) {
        return getDefaultValue(result.getFindings());
    }

    public String getValue(FindResult result, String lang) {
        return result.getFindings().get(getColumnOfLang(lang));
    }
}
