package stni.languager.crawl;

import org.codehaus.plexus.util.IOUtil;

import java.io.*;

/**
 *
 */
public class CopyAction extends AbstractCrawlAction {
    private final File targetDir;

    public CopyAction(File targetDir) {
        this.targetDir = targetDir;
    }

    public void action(File basedir, File file, CrawlPattern pattern) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            final File target = target(file, basedir, targetDir);
            IOUtil.copy(in, new FileOutputStream(new File(target, file.getName())));
        }
    }
}
