package guru.nidi.languager.crawl;

import java.io.File;
import java.util.regex.Matcher;

/**
*
*/
public interface Replacer {
    String replace(File file,Matcher m);
}
