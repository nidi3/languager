package guru.nidi.languager.crawl;

import java.io.File;

/**
 *
 */
public abstract class AbstractCrawlAction implements CrawlAction {
    protected File target(File source, File sourceBaseDir, File targetDir) {
        String relativeSource = source.getParentFile().getAbsolutePath().substring(sourceBaseDir.getAbsolutePath().length());
        File target = new File(targetDir, relativeSource);
        target.mkdirs();
        return target;
    }

}
