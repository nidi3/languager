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
