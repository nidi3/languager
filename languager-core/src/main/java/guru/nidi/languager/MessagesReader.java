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
import java.io.Reader;
import java.util.List;

/**
 *
 */
public class MessagesReader implements AutoCloseable {
    private final CsvReader in;
    private final MessageLine firstParts;

    public MessagesReader(File f, String encoding, char csvSeparator) throws IOException {
        this(Util.reader(f, encoding), csvSeparator);
    }

    public MessagesReader(Reader reader, char csvSeparator) throws IOException {
        this.in = new CsvReader(reader, csvSeparator);
        firstParts = MessageLine.of(toLowerCase(in.readLine()));
        firstParts.checkFirstLine();
    }

    private List<String> toLowerCase(List<String> strings) {
        for (int i = 0; i < strings.size(); i++) {
            strings.set(i, strings.get(i).toLowerCase());
        }
        return strings;
    }

    public MessageLine readLine() throws IOException {
        return MessageLine.of(in.readLine());
    }

    public boolean isEndOfInput() {
        return in.isEndOfInput();
    }

    public MessageLine getFirstParts() {
        return firstParts;
    }

    public void close() throws IOException {
        in.close();
    }
}
