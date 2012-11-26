package stni.languager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 *
 */
public class FindRegexAction extends AbstractContentReadingCrawlAction<RegexCrawlPattern> {

    private final List<FindResult> results = new ArrayList<FindResult>();

    @Override
    protected void doAction(File basedir, File file, String content, RegexCrawlPattern pattern) {
        Matcher matcher = pattern.getRegexPattern().matcher(content);
        while (matcher.find()) {
            List<String> finds = new ArrayList<String>();
            for (int i = 1; i <= matcher.groupCount(); i++) {
                finds.add(matcher.group(i));
            }
            results.add(new FindResult(
                    file.getAbsolutePath(), lineOfPosition(matcher.start()), columnOfPosition(matcher.start()), finds));
        }
    }

    public List<FindResult> getResults() {
        return results;
    }
}
