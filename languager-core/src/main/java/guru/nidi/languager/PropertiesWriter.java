/*
 * Copyright (C) 2014 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.languager;


import java.io.*;
import java.util.regex.Pattern;

import static guru.nidi.languager.Message.Status.NOT_FOUND;

/**
 *
 */
public class PropertiesWriter {
    private static final Pattern NEW_LINE = Pattern.compile("\\r?\\n|\\r", Pattern.MULTILINE);

    private final char csvSeparator;
    private final boolean alwaysUseTextFormat;

    public PropertiesWriter(char csvSeparator, boolean alwaysUseTextFormat) {
        this.csvSeparator = csvSeparator;
        this.alwaysUseTextFormat = alwaysUseTextFormat;
    }

    public PropertiesWriter(char csvSeparator) {
        this(csvSeparator, false);
    }

    public void write(File csv, String csvEncoding, File outputDir, String basename) throws IOException {
        write(new InputStreamReader(new FileInputStream(csv), csvEncoding), omitCommonPrefix(outputDir, csv), outputDir, basename);
    }

    public void write(Reader csv, String source, File outputDir, String basename) throws IOException {
        try (MessagesReader in = new MessagesReader(csv, csvSeparator)) {
            if (!in.isEndOfInput()) {
                BufferedWriter[] out = initPropertiesFiles(source, outputDir, basename, in.getFirstParts());
                writePropertiesFiles(in, out);
                closePropertiesFiles(out);
            }
        }
    }

    private void closePropertiesFiles(BufferedWriter[] out) throws IOException {
        for (BufferedWriter writer : out) {
            writer.close();
        }
    }

    private BufferedWriter[] initPropertiesFiles(String source, File outputDir, String basename, MessageLine firstParts) throws IOException {
        BufferedWriter[] out = new BufferedWriter[firstParts.languageCount()];
        for (int i = 0; i < out.length; i++) {
            String langAppendix = (i == 0 ? "" : ("_" + firstParts.readValue(i, null)));
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
            MessageLine line = in.readLine();
            String key = line.readKey();
            Message.Status status = line.readStatus();
            if (status != NOT_FOUND) {
                String defaultValue = line.readDefaultValue(("?" + key + "?"));
                for (int i = 0; i < out.length; i++) {
                    String val = line.readValue(i, defaultValue);
                    String s = PropertiesUtils.escapeSingleQuotes(NEW_LINE.matcher(val).replaceAll(" \\\\\r\n"), alwaysUseTextFormat);
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
