package stni.languager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class OccurrenceReader {
    private final Map<String, List<String>> occurrences = new HashMap<String, List<String>>();

    public OccurrenceReader(File file) throws IOException {
        final File occFile = OccurrenceWriter.fromMessagesFile(file);
        CsvReader reader = null;
        try {
            reader = new CsvReader(new InputStreamReader(new FileInputStream(occFile), "utf-8"), ';');
            while (!reader.isEndOfInput()) {
                final List<String> line = reader.readLine();
                occurrences.put(line.get(0), line.subList(1, line.size()));
            }
        } finally {
            Util.closeSilently(reader);
        }
    }

    public List<String> getOccurrences(String key) {
        final List<String> res = occurrences.get(key);
        return res == null ? Collections.<String>emptyList() : res;
    }
}
