package stni.languager;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.regex.Pattern;

import static stni.languager.MessagesWriter.DEFAULT_COLUMN;
import static stni.languager.MessagesWriter.KEY_COLUMN;
import static stni.languager.MessagesWriter.KNOWN_COLUMN;

/**
 * Created by IntelliJ IDEA.
 * User: nidi
 * Date: 09.03.12
 * Time: 22:54
 * To change this template use File | Settings | File Templates.
 */
public class PropertiesWriter {
    private static Pattern NEW_LINE = Pattern.compile("\\r?\\n|\\r", Pattern.MULTILINE);

    private final char csvSeparator;

    public PropertiesWriter(char csvSeparator) {
        this.csvSeparator = csvSeparator;
    }

    public void write(File csv, String csvEncoding, File outputDir, String basename) throws IOException {
        write(new InputStreamReader(new FileInputStream(csv), csvEncoding), outputDir, basename);
    }

    public void write(Reader csv, File outputDir, String basename) throws IOException {
        BufferedReader in = new BufferedReader(csv);
        if (in.ready()) {
            List<String> first = new CsvReader(in.readLine().toLowerCase(),csvSeparator).readLine();
            int langs = first.size() - DEFAULT_COLUMN;
            BufferedWriter[] out = new BufferedWriter[langs];
            out[0] = Util.writer(new File(outputDir, basename + ".properties"), Util.ISO);
            for (int i = 1; i < langs; i++) {
                out[i] = Util.writer(new File(outputDir, basename + "_" + first.get(i + DEFAULT_COLUMN) + ".properties"), Util.ISO);
            }
            CsvReader reader = new CsvReader(in,csvSeparator);
            while (in.ready() && !reader.isEndOfInput()) {
                List<String> line = reader.readLine();
                String key = line.get(KEY_COLUMN);
                boolean known = line.size() > KNOWN_COLUMN ? (line.get(KNOWN_COLUMN).length() == 0) : true;
                if (known) {
                    String defaultValue = line.size() > DEFAULT_COLUMN ? line.get(DEFAULT_COLUMN) : ("?" + key + "?");
                    for (int i = 0; i < langs; i++) {
                        String val = defaultValue;
                        if (line.size() > i + DEFAULT_COLUMN && line.get(i + DEFAULT_COLUMN).length() > 0) {
                            val = line.get(i + DEFAULT_COLUMN);
                        }
                        String s = NEW_LINE.matcher(val).replaceAll(" \\\\\r\n");
                        out[i].write(key + "=" + s);
                        out[i].newLine();
                    }
                }
            }
            for (int i = 0; i < langs; i++) {
                out[i].close();
            }
        }
        in.close();
    }
}
