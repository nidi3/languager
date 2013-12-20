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
        BufferedWriter[] out = new BufferedWriter[MessageIO.languageCount(firstParts)];
        for (int i = 0; i < out.length; i++) {
            String langAppendix = (i == 0 ? "" : ("_" + MessageIO.readValue(firstParts, i, null)));
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
            String key = MessageIO.readKey(line);
            Message.Status status = MessageIO.readStatus(line);
            if (status != NOT_FOUND) {
                String defaultValue = MessageIO.readDefaultValue(line, ("?" + key + "?"));
                for (int i = 0; i < out.length; i++) {
                    String val = MessageIO.readValue(line, i, defaultValue);
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
