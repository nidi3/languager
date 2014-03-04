package guru.nidi.languager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;

/**
 *
 */
public class OccurrenceWriter {

    public void write(File file, Collection<Message> messages) throws IOException {
        final File occFile = fromMessagesFile(file);
        try (CsvWriter writer = new CsvWriter(new OutputStreamWriter(new FileOutputStream(occFile), "utf-8"), ';')) {
            for (Message message : messages) {
                writer.writeField(message.getKey());
                for (SourcePosition occurrence : message.getOccurrences()) {
                    writer.writeField(occurrence.getSource().getAbsolutePath() + ":[" + occurrence.getLine() + "," + occurrence.getColumn() + "]");
                }
                writer.writeEndOfLine();
            }
        }
    }

    static File fromMessagesFile(File file) {
        return new File(file.getParentFile(), file.getName().substring(0, file.getName().length() - 4) + "_occ.csv");
    }

}
