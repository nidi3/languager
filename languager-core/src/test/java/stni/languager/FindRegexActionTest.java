package stni.languager;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

/**
 *
 */
public class FindRegexActionTest {
    @Test
    public void testFindStrings() throws Exception {
        System.out.println(new File(".").getAbsolutePath());
        File base = new File("src/test/resources/stni/languager");
        FileCrawler<RegexCrawlPattern, FindRegexAction> crawler = FileCrawler.create(base, new FindRegexAction());
        crawler.addCrawlPattern(new RegexCrawlPattern("<msg key='(.*?)'>(.*?)</msg>", "*.html", null, "utf-8"));
        List<List<String>> res = crawler.crawl().getResults();
        assertEquals(2, res.size());
        assertEquals(2, res.get(0).size());
        assertEquals("key1", res.get(0).get(0));
        assertEquals("default1", res.get(0).get(1));
        assertEquals("key2", res.get(1).get(0));
        assertEquals("default2", res.get(1).get(1));
    }
}
