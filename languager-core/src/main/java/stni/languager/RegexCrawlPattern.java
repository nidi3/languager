package stni.languager;

import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: nidi
 * Date: 09.03.12
 * Time: 23:57
 * To change this template use File | Settings | File Templates.
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
