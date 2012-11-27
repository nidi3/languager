package stni.languager.crawl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;

/**
 *
 */
public class FileCrawler {

    private final CrawlPattern crawlPattern;

    public FileCrawler(CrawlPattern crawlPattern) {
        this.crawlPattern = crawlPattern;
    }

    public <T extends CrawlAction> T crawl(T crawlAction) throws IOException {
        List<File> files = FileUtils.getFiles(crawlPattern.getBasedir(), crawlPattern.getIncludes(), crawlPattern.getExcludes());
        for (File file : files) {
            crawlAction.action(crawlPattern.getBasedir(), file, crawlPattern);
        }
        return crawlAction;
    }

}
