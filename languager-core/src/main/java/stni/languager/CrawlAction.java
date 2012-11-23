package stni.languager;

import java.io.File;
import java.io.IOException;

/**
* Created with IntelliJ IDEA.
* User: nidi
* Date: 16.09.12
* Time: 02:09
* To change this template use File | Settings | File Templates.
*/
public interface CrawlAction<T extends CrawlPattern> {
    void action(File basedir,File file, T pattern) throws IOException;
}
