package stni.languager;


import java.io.*;
import java.util.List;
import java.util.regex.Pattern;

import static stni.languager.Message.Status.NOT_FOUND;

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
        MessagesReader in = null;
        try {
            in = new MessagesReader(csv, csvSeparator);
            if (!in.isEndOfInput()) {
                BufferedWriter[] out = initPropertiesFiles(source, outputDir, basename, in.getFirstParts());
                writePropertiesFiles(in, out);
                closePropertiesFiles(out);
            }
        } catch (IOException e) {
            in.close();
        }
    }

    private void closePropertiesFiles(BufferedWriter[] out) throws IOException {
        for (int i = 0; i < out.length; i++) {
            out[i].close();
        }
    }

    private BufferedWriter[] initPropertiesFiles(String source, File outputDir, String basename, List<String> firstParts) throws IOException {
        int langs = firstParts.size() - MessageIO.DEFAULT_COLUMN;
        BufferedWriter[] out = new BufferedWriter[langs];

        for (int i = 0; i < out.length; i++) {
            String langAppendix = (i == 0 ? "" : ("_" + firstParts.get(i + MessageIO.DEFAULT_COLUMN)));
            out[i] = Util.writer(new File(outputDir, basename + langAppendix + ".properties"), Util.ISO);
            out[i].write("# This file is generated from " + source);
            out[i].newLine();
            out[i].write("# Do NOT edit manually!");
            out[i].newLine();
        }
        return out;
    }

    private void writePropertiesFiles(MessagesReader in, BufferedWriter[] out) throws IOException {
        while (!in.isEndOfInput()) {
            List<String> line = in.readLine();
            String key = line.get(MessageIO.KEY_COLUMN);
            Message.Status status = MessageIO.statusOfLine(line);
            if (status != NOT_FOUND) {
                String defaultValue = line.size() > MessageIO.DEFAULT_COLUMN ? line.get(MessageIO.DEFAULT_COLUMN) : ("?" + key + "?");
                for (int i = 0; i < out.length; i++) {
                    String val = defaultValue;
                    if (line.size() > i + MessageIO.DEFAULT_COLUMN && line.get(i + MessageIO.DEFAULT_COLUMN).length() > 0) {
                        val = line.get(i + MessageIO.DEFAULT_COLUMN);
                    }
                    String s = NEW_LINE.matcher(val).replaceAll(" \\\\\r\n");
                    out[i].write(key + "=" + s);
                    out[i].newLine();
                }
            }
        }
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
