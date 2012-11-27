package stni.languager;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public interface CrawlAction {
    void action(File basedir, File file, CrawlPattern pattern) throws IOException;
}
