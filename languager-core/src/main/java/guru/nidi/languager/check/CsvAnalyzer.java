package guru.nidi.languager.check;

import guru.nidi.languager.FindResult;
import guru.nidi.languager.MessageLine;
import guru.nidi.languager.SourcePosition;
import guru.nidi.languager.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CsvAnalyzer {
    private final File file;
    private final List<MessageLine> contents;

    public CsvAnalyzer(File file, List<MessageLine> contents) {
        this.file = file;
        this.contents = contents;
    }

    public CsvAnalyzer(File file, String encoding, char separator) throws IOException {
        this(file, Util.readCsvFile(file, encoding, separator));
    }

    public List<FindResult<MessageLine>> compareDefaultValueWithLanguage(String lang) {
        int language = contents.get(0).findLang(lang);
        List<FindResult<MessageLine>> res = new ArrayList<>();
        int lineNum = 1;
        for (MessageLine line : contents.subList(1, contents.size())) {
            lineNum++;
            String entry = line.readValue(language, "");
            if (entry.length() > 0 && !entry.equals(line.readDefaultValue(null))) {
                res.add(new FindResult<>(new SourcePosition(file, lineNum, 1), line));
            }
        }
        return res;
    }
}
