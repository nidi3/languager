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
package guru.nidi.languager.crawl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public abstract class AbstractContentReadingCrawlAction extends AbstractCrawlAction {

    private Integer[] newlines;

    public void action(File basedir, File file, CrawlPattern pattern) throws IOException {
        try (InputStreamReader in = new InputStreamReader(new FileInputStream(file), pattern.getEncoding())) {
            char[] ch = new char[(int) file.length()];
            int read = in.read(ch);
            String s = new String(ch, 0, read);
            findNewlines(s);
            doAction(basedir, file, s, pattern);
        }
    }

    protected abstract void doAction(File basedir, File file, String content, CrawlPattern pattern) throws IOException;

    protected void findNewlines(String content) {
        List<Integer> newlineList = new ArrayList<>();
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '\n') {
                newlineList.add(i);
            }
        }
        newlines = newlineList.toArray(new Integer[newlineList.size()]);
    }

    protected int lineOfPosition(int pos) {
        return -Arrays.binarySearch(newlines, pos);
    }

    protected int columnOfPosition(int pos) {
        final int line = lineOfPosition(pos);
        return line == 1 ? pos : pos - newlines[line - 2];
    }
}
