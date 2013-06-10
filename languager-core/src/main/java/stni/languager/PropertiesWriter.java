package stni.languager;


import java.io.*;
import java.util.List;
import java.util.regex.Pattern;

import static stni.languager.Message.Status.NOT_FOUND;
import static stni.languager.MessagesWriter.DEFAULT_COLUMN;
import static stni.languager.MessagesWriter.KEY_COLUMN;

/**
 *
 */
public class PropertiesWriter {
    private static Pattern NEW_LINE = Pattern.compile("\\r?\\n|\\r", Pattern.MULTILINE);

    private final char csvSeparator;

    public PropertiesWriter(char csvSeparator) {
        this.csvSeparator = csvSeparator;
    }

    public void write(File csv, String csvEncoding, File outputDir, String basename) throws IOException {
        write(new InputStreamReader(new FileInputStream(csv), csvEncoding), omitCommonPrefix(outputDir, csv), outputDir, basename);
    }

    public void write(Reader csv, String source, File outputDir, String basename) throws IOException {
        BufferedReader in = new BufferedReader(csv);
        if (in.ready()) {
            List<String> first = new CsvReader(in.readLine().toLowerCase(), csvSeparator).readLine();
            int langs = first.size() - DEFAULT_COLUMN;
            BufferedWriter[] out = new BufferedWriter[langs];

            for (int i = 0; i < langs; i++) {
                out[i] = Util.writer(new File(outputDir, basename + (i == 0 ? "" : "_" + first.get(i + DEFAULT_COLUMN)) + ".properties"), Util.ISO);
                out[i].write("# This file is generated from " + source);
                out[i].newLine();
                out[i].write("# Do NOT edit manually!");
                out[i].newLine();
            }
            CsvReader reader = new CsvReader(in, csvSeparator);
            while (in.ready() && !reader.isEndOfInput()) {
                List<String> line = reader.readLine();
                String key = line.get(KEY_COLUMN);
                Message.Status status = MessagesWriter.statusOfLine(line);
                if (status != NOT_FOUND) {
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

    private String omitCommonPrefix(File prefix, File toOmit) {
        String prefixPath = prefix.getAbsolutePath();
        String omitPath = toOmit.getAbsolutePath();
        int i = 0;
        while (prefixPath.charAt(i) == omitPath.charAt(i) && i < Math.min(prefixPath.length(), omitPath.length()) - 1) {
            i++;
        }
        return omitPath.substring(i);
    }
}
