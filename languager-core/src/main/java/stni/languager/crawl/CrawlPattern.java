package stni.languager.crawl;

import java.io.File;

/**
 *
 */
public class CrawlPattern {
    private final File basedir;
    private final String includes;
    private final String excludes;
    private final String encoding;

    public CrawlPattern(File basedir, String includes, String excludes, String encoding) {
        this.basedir = basedir;
        this.includes = includes;
        this.excludes = excludes;
        this.encoding = encoding;
    }

    public File getBasedir() {
        return basedir;
    }

    public String getIncludes() {
        return includes;
    }

    public String getExcludes() {
        return excludes;
    }

    public String getEncoding() {
        return encoding;
    }

}
