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

import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Util {
    final static String ISO = "iso-8859-1";

    static BufferedReader reader(File file, String encoding) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
    }

    static BufferedWriter writer(File file, String encoding) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
    }

    public static List<MessageLine> readCsvFile(File file, String encoding, char separator) throws IOException {
        final List<MessageLine> res = new ArrayList<>();
        try (CsvReader in = new CsvReader(new InputStreamReader(new FileInputStream(file), encoding), separator)) {
            while (!in.isEndOfInput()) {
                res.add(MessageLine.of(in.readLine()));
            }
            return res;
        }
    }

    public static List<File> getFiles(File dir, String includes, String excludes) throws IOException {
        @SuppressWarnings("unchecked")
        List<File> files = FileUtils.getFiles(dir, includes, excludes);
        return files;
    }

}
