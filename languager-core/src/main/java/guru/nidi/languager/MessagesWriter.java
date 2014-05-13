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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static guru.nidi.languager.Message.Status.*;

/**
 *
 */
public class MessagesWriter {
    private final String encoding;
    private final char csvSeparator;

    public MessagesWriter(String encoding, char csvSeparator) {
        this.encoding = encoding;
        this.csvSeparator = csvSeparator;
    }

    public void write(File f, List<Message> msgs) throws IOException {
        SortedMap<String, Message> entries = new TreeMap<>();
        for (Message msg : msgs) {
            entries.put(msg.getKey(), msg);
        }
        write(f, entries);
    }

    public void write(File f, SortedMap<String, Message> msgs) throws IOException {
        MessageLine firstParts = (f.exists() && f.length() > 0) ? readMessages(f, msgs) : defaultFirstParts();
        writeMessages(f, firstParts, msgs);
    }

    private MessageLine defaultFirstParts() {
        return MessageLine.firstLine("en", "de");
    }

    private MessageLine readMessages(File f, SortedMap<String, Message> msgs) throws IOException {
        try (MessagesReader in = new MessagesReader(f, encoding, csvSeparator)) {
            while (!in.isEndOfInput()) {
                MessageLine line = in.readLine();
                if (!line.isEmpty()) {
                    String key = line.readKey();
                    Message.Status status = line.readStatus();
                    String defaultValue = line.readDefaultValue(null);
                    Message foundMessage = msgs.get(key);
                    Message merged;
                    if (foundMessage == null) {
                        merged = new Message(key, status == MANUAL ? MANUAL : NOT_FOUND, defaultValue);
                    } else {
                        merged = new Message(key, status == MANUAL ? MANUAL : FOUND, foundMessage.getDefaultValue() == null ? defaultValue : foundMessage.getDefaultValue());
                        merged.addOccurrences(foundMessage.getOccurrences());
                    }
                    line.readValuesIntoMessage(in.getFirstParts(), merged);
                    msgs.put(key, merged);
                }
            }
            return in.getFirstParts();
        }
    }

    private void writeMessages(File f, MessageLine firstParts, SortedMap<String, Message> msgs) throws IOException {
        try (CsvWriter out = new CsvWriter(Util.writer(f, encoding), csvSeparator)) {
            out.writeLine(firstParts);
            writeLine(out, firstParts, msgs.values());
        }
    }

    private void writeLine(CsvWriter out, MessageLine langs, Collection<Message> msgs) throws IOException {
        for (Message msg : msgs) {
            out.writeField(msg.getKey());
            out.writeField("" + msg.getStatus().getSymbol());
            out.writeField(simpleOccurrencesOf(msg));
            out.writeField(msg.getDefaultValueOrLang());
            langs.writeValues(msg, out);
            out.writeEndOfLine();
        }
    }

    private String simpleOccurrencesOf(Message msg) {
        boolean first = true;
        String res = "";
        for (SourcePosition occ : msg.getOccurrences()) {
            if (first) {
                first = false;
            } else {
                res += ",";
            }
            res += occ.getSource().getName();
        }
        return res;
    }
}
