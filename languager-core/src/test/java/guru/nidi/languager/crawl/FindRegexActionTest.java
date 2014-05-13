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

import guru.nidi.languager.BaseTest;
import guru.nidi.languager.FindResult;
import guru.nidi.languager.SourcePosition;
import org.junit.Test;

import java.io.File;
import java.util.EnumSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class FindRegexActionTest extends BaseTest {
    @Test
    public void testFindMsgStrings() throws Exception {
        File base = fromTestDir("");
        FileCrawler crawler = new FileCrawler(new CrawlPattern(base, "*.html", "*2*,inner*,p*", "utf-8"));
        List<FindResult<List<String>>> res = crawler.crawl(new FindRegexAction("<msg key='(.*?)'>(.*?)</msg>", null, null)).getResults();
        assertEquals(2, res.size());

        final List<String> find0 = res.get(0).getFinding();
        assertEquals(2, find0.size());
        assertEquals("key1", find0.get(0));
        assertEquals("default1", find0.get(1));

        final SourcePosition pos0 = res.get(0).getPosition();
        assertEquals(3, pos0.getLine());
        assertEquals(15, pos0.getColumn());

        final List<String> find1 = res.get(1).getFinding();
        assertEquals("key3", find1.get(0));
        assertEquals("default3", find1.get(1));

        final SourcePosition pos1 = res.get(1).getPosition();
        assertEquals(4, pos1.getLine());
        assertEquals(15, pos1.getColumn());
    }

    @Test
    public void testFindRawStrings() throws Exception {
        File base = fromTestDir("");
        FileCrawler crawler = new FileCrawler(new CrawlPattern(base, "test.html", null, "utf-8"));
        List<FindResult<List<String>>> res = crawler.crawl(new FindRegexAction(">(.*?)<", null, EnumSet.of(FindRegexAction.Flag.TRIM))).getResults();
        assertEquals(4, res.size());
        assertEquals("Test1", res.get(0).getFinding().get(0).trim());
        assertEquals("default1", res.get(1).getFinding().get(0).trim());
        assertEquals("Test2", res.get(2).getFinding().get(0).trim());
        assertEquals("default3", res.get(3).getFinding().get(0).trim());
    }


}
