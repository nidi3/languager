package stni.languager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;

/**
 *
 */
public class FileCrawler<S extends CrawlAction> {

    private final S crawlAction;
    private File basedir;
    private List<CrawlPattern> patterns = new ArrayList<CrawlPattern>();

    private FileCrawler(File basedir, S crawlAction) {
        this.basedir = basedir;
        this.crawlAction = crawlAction;
    }

    public static <S extends CrawlAction> FileCrawler<S> create(File basedir, S crawlAction) {
        return new FileCrawler<S>(basedir, crawlAction);
    }

    public void addCrawlPattern(CrawlPattern pattern) {
        patterns.add(pattern);
    }

    public void addCrawlPatterns(List<CrawlPattern> patterns) {
        this.patterns.addAll(patterns);
    }

    public S crawl() throws IOException {
        for (CrawlPattern pattern : patterns) {
            List<File> files = FileUtils.getFiles(basedir, pattern.getIncludes(), pattern.getExcludes());
            for (File file : files) {
                crawlAction.action(basedir, file, pattern);
            }
        }
        return crawlAction;
    }

}
