package stni.languager.crawl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import stni.languager.BaseTest;

/**
 *
 */
public class FindRegexActionTest extends BaseTest {
    @Test
    public void testFindMsgStrings() throws Exception {
        File base = fromTestDir("");
        FileCrawler crawler = new FileCrawler(new CrawlPattern(base, "*.html", "*2*,inner*", "utf-8"));
        List<FindResult> res = crawler.crawl(new FindRegexAction("<msg key='(.*?)'>(.*?)</msg>", null)).getResults();
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
        File base = fromTestDir("");
        FileCrawler crawler = new FileCrawler(new CrawlPattern(base, "*.html", "test_*,*2*,inner*", "utf-8"));
        List<FindResult> res = crawler.crawl(new FindRegexAction(">(.*?)<", EnumSet.of(FindRegexAction.Flag.TRIM))).getResults();
        assertEquals(4, res.size());
        assertEquals("Test1", res.get(0).getFindings().get(0).trim());
        assertEquals("default1", res.get(1).getFindings().get(0).trim());
        assertEquals("Test2", res.get(2).getFindings().get(0).trim());
        assertEquals("default2", res.get(3).getFindings().get(0).trim());
    }


}
