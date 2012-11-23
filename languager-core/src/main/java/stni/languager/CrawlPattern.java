package stni.languager;

/**
 *
 */
public class CrawlPattern {
    private String includes;
    private String excludes;
    private String encoding;

    public CrawlPattern() {
    }

    public CrawlPattern(String includes, String excludes, String encoding) {
        this.includes = includes;
        this.excludes = excludes;
        this.encoding = encoding;
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

    public void setIncludes(String includes) {
        this.includes = includes;
    }

    public void setExcludes(String excludes) {
        this.excludes = excludes;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
