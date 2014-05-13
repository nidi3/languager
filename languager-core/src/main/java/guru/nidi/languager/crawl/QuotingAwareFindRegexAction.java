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

import guru.nidi.languager.FindResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class QuotingAwareFindRegexAction extends AbstractContentReadingCrawlAction {

    private final List<FindResult> results = new ArrayList<>();
    private Integer[] quoteStart;
    private Integer[] quoteEnd;

    @Override
    protected void doAction(File basedir, File file, String content, CrawlPattern pattern) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

//    protected void doAction(File basedir, File file, String content, QuotingRegexCrawlPattern pattern) {
//        findQuotes(content);
//        Matcher matcher = pattern.getRegexPattern().matcher(content);
//        while (matcher.find()) {
//            if (isPositionInQuote(matcher.start()) == pattern.isInQuotes()) {
//                List<String> finds = new ArrayList<String>();
//                for (int i = 1; i <= matcher.groupCount(); i++) {
//                    finds.add(matcher.group(i));
//                }
//                results.add(new FindResult(
//                        file.getAbsolutePath(), lineOfPosition(matcher.start()), columnOfPosition(matcher.start()), finds));
//            }
//        }
//    }

    private void findQuotes(String content) {
        List<Integer> quoteStartList = new ArrayList<>();
        List<Integer> quoteEndList = new ArrayList<>();
        boolean inQuote = false;
        for (int i = 0; i < content.length(); i++) {
            if (!inQuote && content.charAt(i) == '<') {
                inQuote = true;
                quoteStartList.add(i);
            }
            if (inQuote && content.charAt(i) == '>') {
                inQuote = false;
                quoteEndList.add(i);
            }
        }
        quoteStart = quoteStartList.toArray(new Integer[quoteStartList.size()]);
        quoteEnd = quoteEndList.toArray(new Integer[quoteEndList.size()]);
    }

    private boolean isPositionInQuote(int pos) {
        int startBefore = -Arrays.binarySearch(quoteStart, pos);
        return quoteEnd[startBefore] > pos;
    }

    public List<FindResult> getResults() {
        return results;
    }
}
