package stni.languager;

import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: stni
 * Date: 09.03.12
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
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
