package stni.languager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 */
public abstract class AbstractContentReadingCrawlAction<T extends CrawlPattern> implements CrawlAction<T> {
    public void action(File basedir, File file, T pattern) throws IOException {
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(new FileInputStream(file), pattern.getEncoding());
            char[] ch = new char[(int) file.length()];
            int read = in.read(ch);
            String s = new String(ch, 0, read);
            doAction(basedir, file, s, pattern);
        } catch (IOException e) {
            Util.closeSilently(in);
        }
    }

    protected abstract void doAction(File basedir, File file, String content, T pattern) throws IOException;
}
