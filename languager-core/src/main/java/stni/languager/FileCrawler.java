package stni.languager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;

/**
 *
 */
public class FileCrawler<T extends CrawlPattern, S extends CrawlAction<T>> {

    private final S crawlAction;
    private File basedir;
    private List<T> patterns = new ArrayList<T>();

    private FileCrawler(File basedir, S crawlAction) {
        this.basedir = basedir;
        this.crawlAction = crawlAction;
    }

    public static <T extends CrawlPattern, S extends CrawlAction<T>> FileCrawler<T, S> create(File basedir, S crawlAction) {
        return new FileCrawler<T, S>(basedir, crawlAction);
    }

    public void addCrawlPattern(T pattern) {
        patterns.add(pattern);
    }

    public void addCrawlPatterns(List<T> patterns) {
        this.patterns.addAll(patterns);
    }

    public S crawl() throws IOException {
        for (T pattern : patterns) {
            List<File> files = FileUtils.getFiles(basedir, pattern.getIncludes(), pattern.getExcludes());
            for (File file : files) {
                crawlAction.action(basedir, file, pattern);
            }
        }
        return crawlAction;
    }

}
