package stni.languager;

/**
 * Created by IntelliJ IDEA.
 * User: nidi
 * Date: 09.03.12
 * Time: 23:57
 * To change this template use File | Settings | File Templates.
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
