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

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CsvReader implements Closeable {
    private static final char EOI = (char) -1;
    private final Reader in;
    private final char separator;
    private char c;

    public CsvReader(Reader in, char separator) throws IOException {
        this.in = in;
        this.separator = separator;
        read();
    }

    public CsvReader(String in, char separator) throws IOException {
        this(new StringReader(in), separator);
    }

    private void read() throws IOException {
        c = (char) in.read();
    }

    private String unescapeCsv(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }
        return value;
    }

    public MessageLine readMessageLine() throws IOException {
        return MessageLine.of(readLine());
    }

    public List<String> readLine() throws IOException {
        List<String> res = new ArrayList<>();
        StringBuilder curr = new StringBuilder();
        boolean quote = false;
        f:
        while (c != EOI) {
            if (c == separator) {
                if (!quote) {
                    res.add(unescapeCsv(curr.toString()));
                    curr = new StringBuilder();
                } else {
                    curr.append(c);
                }
                read();
            } else {
                switch (c) {
                    case '\r':
                    case '\n':
                        if (!quote) {
                            do {
                                read();
                            } while ((c == '\r' || c == '\n') && c != EOI);
                            break f;
                        } else {
                            curr.append(c);
                            read();
                        }
                        break;
                    case '"':
                        do {
                            quote = !quote;
                            if (c != EOI) {
                                curr.append(c);
                            }
                            read();
                        } while (c == '"');
                        break;
                    default:
                        curr.append(c);
                        read();
                        break;
                }
            }
        }
        res.add(unescapeCsv(curr.toString()));
        return res;
    }

    public boolean isEndOfInput() {
        return c == EOI;
    }

    public void close() throws IOException {
        in.close();
    }
}
