package guru.nidi.languager.crawl;

import java.io.File;

/**
 *
 */
public class CrawlPattern {
    private static final String DEFAULT_ENCODING = "utf-8";

    private final File basedir;
    private final String includes;
    private final String excludes;
    private final String encoding;

    public CrawlPattern(File basedir, String includes, String excludes, String encoding) {
        this.basedir = basedir;
        this.includes = includes;
        this.excludes = excludes;
        this.encoding = encoding != null ? encoding : DEFAULT_ENCODING;
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
