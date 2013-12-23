package stni.languager.check;

import stni.languager.FindResult;
import stni.languager.MessageLine;
import stni.languager.SourcePosition;
import stni.languager.Util;

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

    public CsvAnalyzer(File file, List<MessageLine> contents) throws IOException {
        this.file = file;
        this.contents = contents;
    }

    public CsvAnalyzer(File file, String encoding, char separator) throws IOException {
        this(file, Util.readCsvFile(file, encoding, separator));
    }

    public List<FindResult<MessageLine>> compareDefaultValueWithLanguage(String lang) throws IOException {
        int language = contents.get(0).findLang(lang);
        List<FindResult<MessageLine>> res = new ArrayList<FindResult<MessageLine>>();
        int lineNum = 1;
        for (MessageLine line : contents.subList(1, contents.size())) {
            lineNum++;
            String entry = line.readValue(language, "");
            if (entry.length() > 0 && !entry.equals(line.readDefaultValue(null))) {
                res.add(new FindResult<MessageLine>(new SourcePosition(file, 0, 0, lineNum, 1), line));
            }
        }
        return res;
    }
}
