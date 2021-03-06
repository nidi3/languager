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
package guru.nidi.languager.check;

import guru.nidi.languager.FindResult;
import guru.nidi.languager.MessageLine;
import guru.nidi.languager.SourcePosition;
import guru.nidi.languager.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CsvAnalyzer {
    private final File file;
    private final List<MessageLine> contents;

    public CsvAnalyzer(File file, List<MessageLine> contents) {
        this.file = file;
        this.contents = contents;
    }

    public CsvAnalyzer(File file, String encoding, char separator) throws IOException {
        this(file, Util.readCsvFile(file, encoding, separator));
    }

    public List<FindResult<MessageLine>> compareDefaultValueWithLanguage(String lang) {
        int language = contents.get(0).findLang(lang);
        List<FindResult<MessageLine>> res = new ArrayList<>();
        int lineNum = 1;
        for (MessageLine line : contents.subList(1, contents.size())) {
            lineNum++;
            String entry = line.readValue(language, "");
            if (entry.length() > 0 && !entry.equals(line.readDefaultValue(null))) {
                res.add(new FindResult<>(new SourcePosition(file, lineNum, 1), line));
            }
        }
        return res;
    }
}
