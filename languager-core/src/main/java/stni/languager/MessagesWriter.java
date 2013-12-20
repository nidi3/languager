package stni.languager;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static stni.languager.Message.Status.*;

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
        SortedMap<String, Message> entries = new TreeMap<String, Message>();
        for (Message msg : msgs) {
            entries.put(msg.getKey(), msg);
        }
        write(f, entries);
    }

    public void write(File f, SortedMap<String, Message> msgs) throws IOException {
        List<String> firstParts = (f.exists() && f.length() > 0) ? readMessages(f, msgs) : defaultFirstParts();
        writeMessages(f, firstParts, msgs);
    }

    private List<String> defaultFirstParts() throws IOException {
        return MessageIO.firstLine("en", "de");
    }

    private List<String> readMessages(File f, SortedMap<String, Message> msgs) throws IOException {
        MessagesReader in = null;
        try {
            in = new MessagesReader(f, encoding, csvSeparator);
            while (!in.isEndOfInput()) {
                List<String> line = in.readLine();
                if (line.size() > 1 || line.get(0).trim().length() > 0) {
                    String key = MessageIO.readKey(line);
                    Message.Status status = MessageIO.readStatus(line);
                    String defaultValue = MessageIO.readDefaultValue(line, null);
                    Message foundMessage = msgs.get(key);
                    Message merged;
                    if (foundMessage == null) {
                        merged = new Message(key, status == MANUAL ? MANUAL : NOT_FOUND, defaultValue);
                    } else {
                        merged = new Message(key, status == MANUAL ? MANUAL : FOUND, foundMessage.getDefaultValue() == null ? defaultValue : foundMessage.getDefaultValue());
                        merged.addOccurrences(foundMessage.getOccurrences());
                    }
                    MessageIO.readValuesIntoMessage(line,in.getFirstParts(),merged);
                    msgs.put(key, merged);
                }
            }
            return in.getFirstParts();
        } finally {
            Util.closeSilently(in);
        }
    }

    private void writeMessages(File f, List<String> firstParts, SortedMap<String, Message> msgs) throws IOException {
        CsvWriter out = null;
        try {
            out = new CsvWriter(Util.writer(f, encoding), csvSeparator);
            out.writeLine(firstParts);
            writeLine(out, firstParts, msgs.values());
        } finally {
            Util.closeSilently(out);
        }
    }

    private void writeLine(CsvWriter out, List<String> langs, Collection<Message> msgs) throws IOException {
        for (Message msg : msgs) {
            out.writeField(msg.getKey());
            out.writeField("" + msg.getStatus().getSymbol());
            out.writeField(simpleOccurrencesOf(msg));
            out.writeField(msg.getDefaultValueOrLang());
            MessageIO.writeValues(langs, msg, out);
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
