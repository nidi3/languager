package stni.languager.crawl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;

/**
 *
 */
public class FileCrawler {

    private List<CrawlPattern> patterns = new ArrayList<CrawlPattern>();

    public void addCrawlPattern(CrawlPattern pattern) {
        patterns.add(pattern);
    }

    public void addCrawlPatterns(List<CrawlPattern> patterns) {
        this.patterns.addAll(patterns);
    }

    public <T extends CrawlAction> T crawl(T crawlAction) throws IOException {
        for (CrawlPattern pattern : patterns) {
            List<File> files = FileUtils.getFiles(pattern.getBasedir(), pattern.getIncludes(), pattern.getExcludes());
            for (File file : files) {
                crawlAction.action(pattern.getBasedir(), file, pattern);
            }
        }
        return crawlAction;
    }

}
