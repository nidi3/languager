package stni.languager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class FindRegexAction extends AbstractContentReadingCrawlAction {

    private final List<FindResult> results = new ArrayList<FindResult>();

    private final Pattern regex;
    private final boolean withEmpty;

    public FindRegexAction(String regex, boolean withEmpty) {
        this.regex = Pattern.compile(regex, Pattern.DOTALL);
        this.withEmpty = withEmpty;
    }

    @Override
    protected void doAction(File basedir, File file, String content, CrawlPattern pattern) throws IOException {
        Matcher matcher = regex.matcher(content);
        while (matcher.find()) {
            if (withEmpty || matcher.group(1).trim().length() > 0) {
                List<String> finds = new ArrayList<String>();
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    finds.add(matcher.group(i));
                }
                results.add(new FindResult(
                        file.getAbsolutePath(), lineOfPosition(matcher.start()), columnOfPosition(matcher.start()), finds));
            }
        }
    }

    public List<FindResult> getResults() {
        return results;
    }

    public Pattern getRegex() {
        return regex;
    }

    public boolean isWithEmpty() {
        return withEmpty;
    }
}
