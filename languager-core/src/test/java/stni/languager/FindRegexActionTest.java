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
    public void testFindMsgStrings() throws Exception {
        File base = baseDir("stni/languager");
        FileCrawler<FindRegexAction> crawler = FileCrawler.create(base, new FindRegexAction("<msg key='(.*?)'>(.*?)</msg>", false));
        crawler.addCrawlPattern(new CrawlPattern("*.html", null, "utf-8"));
        List<FindResult> res = crawler.crawl().getResults();
        assertEquals(2, res.size());
        assertEquals(2, res.get(0).getFindings().size());
        assertEquals("key1", res.get(0).getFindings().get(0));
        assertEquals("default1", res.get(0).getFindings().get(1));
        assertEquals(3, res.get(0).getLine());
        assertEquals(15, res.get(0).getColumn());
        assertEquals("key2", res.get(1).getFindings().get(0));
        assertEquals("default2", res.get(1).getFindings().get(1));
        assertEquals(4, res.get(1).getLine());
        assertEquals(15, res.get(1).getColumn());
    }

    @Test
    public void testFindRawStrings() throws Exception {
        File base = baseDir("stni/languager");
        FileCrawler<FindRegexAction> crawler = FileCrawler.create(base, new FindRegexAction(">(.*?)<", false));
        crawler.addCrawlPattern(new CrawlPattern("*.html", null, "utf-8"));
        List<FindResult> res = crawler.crawl().getResults();
        assertEquals(4, res.size());
        assertEquals("Test1", res.get(0).getFindings().get(0).trim());
        assertEquals("default1", res.get(1).getFindings().get(0).trim());
        assertEquals("Test2", res.get(2).getFindings().get(0).trim());
        assertEquals("default2", res.get(3).getFindings().get(0).trim());
    }

    private File baseDir(String relativeToTest) {
        File base = new File("src/test/resources/" + relativeToTest);
        if (!base.exists()) {
            base = new File("languager-core/src/test/resources/" + relativeToTest);
        }
        return base;
    }
}
