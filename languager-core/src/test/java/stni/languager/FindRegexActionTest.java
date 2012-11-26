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
        File base = baseDir("stni/languager");
        FileCrawler<RegexCrawlPattern, FindRegexAction> crawler = FileCrawler.create(base, new FindRegexAction());
        crawler.addCrawlPattern(new RegexCrawlPattern("<msg key='(.*?)'>(.*?)</msg>", "*.html", null, "utf-8"));
        List<FindResult> res = crawler.crawl().getResults();
        assertEquals(2, res.size());
        assertEquals(2, res.get(0).getFindings().size());
        assertEquals("key1", res.get(0).getFindings().get(0));
        assertEquals("default1", res.get(0).getFindings().get(1));
        assertEquals(3, res.get(0).getLine());
        assertEquals(14, res.get(0).getColumn());
        assertEquals("key2", res.get(1).getFindings().get(0));
        assertEquals("default2", res.get(1).getFindings().get(1));
        assertEquals(4, res.get(1).getLine());
        assertEquals(14, res.get(1).getColumn());
    }

    private File baseDir(String relativeToTest) {
        File base = new File("src/test/resources/" + relativeToTest);
        if (!base.exists()) {
            base = new File("languager-core/src/test/resources/" + relativeToTest);
        }
        return base;
    }
}
