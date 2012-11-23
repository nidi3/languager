package stni.languager;

import java.io.File;
import java.io.IOException;

/**
*
*/
public interface CrawlAction<T extends CrawlPattern> {
    void action(File basedir,File file, T pattern) throws IOException;
}
