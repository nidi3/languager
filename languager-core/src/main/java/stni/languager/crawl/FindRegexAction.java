package stni.languager.crawl;

import static stni.languager.crawl.FindRegexAction.Flag.TRIM;
import static stni.languager.crawl.FindRegexAction.Flag.WITH_EMPTY;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class FindRegexAction extends AbstractContentReadingCrawlAction {
    public enum Flag {
        WITH_EMPTY, TRIM
    }

    private final List<FindResult> results = new ArrayList<FindResult>();

    private final Pattern regex;
    private final EnumSet<Flag> flags;

    public FindRegexAction(String regex, EnumSet<Flag> flags) {
        this.regex = Pattern.compile(regex, Pattern.DOTALL);
        if (regex.indexOf('(') < 0 || regex.indexOf(')') < 0) {
            throw new IllegalArgumentException("Regex must contain at least one group");
        }
        this.flags = flags == null ? EnumSet.noneOf(Flag.class) : flags;
    }

    @Override
    protected void doAction(File basedir, File file, String content, CrawlPattern pattern) throws IOException {
        Matcher matcher = regex.matcher(content);
        while (matcher.find()) {
            if (isValidMatch(matcher)) {
                List<String> finds = new ArrayList<String>();
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    finds.add(group(matcher, i));
                }
                results.add(new FindResult(
                        file, matcher.start(), matcher.end(),
                        lineOfPosition(matcher.start()), columnOfPosition(matcher.start()), finds));
            }
        }
    }

    protected boolean isValidMatch(Matcher matcher) {
        return flags.contains(WITH_EMPTY) || group(matcher, 1).length() > 0;
    }

    protected String group(Matcher m, int index) {
        final String group = m.group(index);
        return flags.contains(TRIM) ? group.trim() : group;
    }

    public List<FindResult> getResults() {
        return results;
    }

    public Pattern getRegex() {
        return regex;
    }

    public EnumSet<Flag> getFlags() {
        return flags;
    }
}
