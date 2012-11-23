package stni.languager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: stni
 * Date: 09.03.12
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
public class FindRegexAction extends AbstractContentReadingCrawlAction<RegexCrawlPattern> {

    private final List<List<String>> results = new ArrayList<List<String>>();

    @Override
    protected void doAction(File basedir, File file, String content, RegexCrawlPattern pattern) {
        Matcher matcher = pattern.getRegexPattern().matcher(content);
        while (matcher.find()) {
            List<String> finds = new ArrayList<String>();
            for (int i = 1; i <= matcher.groupCount(); i++) {
                finds.add(matcher.group(i));
            }
            results.add(finds);
        }
    }

    public List<List<String>> getResults() {
        return results;
    }
}
