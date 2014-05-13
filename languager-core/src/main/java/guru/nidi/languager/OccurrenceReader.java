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
    private final Map<String, List<String>> occurrences = new HashMap<>();

    public OccurrenceReader(File file) throws IOException {
        final File occFile = OccurrenceWriter.fromMessagesFile(file);
        try (CsvReader reader = new CsvReader(new InputStreamReader(new FileInputStream(occFile), "utf-8"), ';')) {
            while (!reader.isEndOfInput()) {
                final List<String> line = reader.readLine();
                occurrences.put(line.get(0), line.subList(1, line.size()));
            }
        }
    }

    public List<String> getOccurrences(String key) {
        final List<String> res = occurrences.get(key);
        return res == null ? Collections.<String>emptyList() : res;
    }
}
