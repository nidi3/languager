package stni.languager;

import java.util.regex.Pattern;

/**
 *
 */
public class RegexCrawlPattern extends CrawlPattern {
    private Pattern regex;

    public RegexCrawlPattern() {
    }

    public RegexCrawlPattern(String regex, String includes, String excludes, String encoding) {
        super(includes, excludes, encoding);
        setRegex(regex);
    }

    public String getRegex() {
        return regex.pattern();
    }

    public Pattern getRegexPattern() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = Pattern.compile(regex,Pattern.DOTALL);
    }

}
