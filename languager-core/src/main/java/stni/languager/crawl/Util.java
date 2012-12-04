package stni.languager.crawl;

import java.io.Closeable;
import java.io.IOException;

/**
 *
 */
class Util {
    static void closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }
}
