package stni.languager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

/**
 *
 */
public class QuotingAwareFindRegexAction extends AbstractContentReadingCrawlAction {

    private final List<FindResult> results = new ArrayList<FindResult>();
    private Integer[] quoteStart;
    private Integer[] quoteEnd;

    @Override
    protected void doAction(File basedir, File file, String content, CrawlPattern pattern) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

//    protected void doAction(File basedir, File file, String content, QuotingRegexCrawlPattern pattern) {
//        findQuotes(content);
//        Matcher matcher = pattern.getRegexPattern().matcher(content);
//        while (matcher.find()) {
//            if (isPositionInQuote(matcher.start()) == pattern.isInQuotes()) {
//                List<String> finds = new ArrayList<String>();
//                for (int i = 1; i <= matcher.groupCount(); i++) {
//                    finds.add(matcher.group(i));
//                }
//                results.add(new FindResult(
//                        file.getAbsolutePath(), lineOfPosition(matcher.start()), columnOfPosition(matcher.start()), finds));
//            }
//        }
//    }

    private void findQuotes(String content) {
        List<Integer> quoteStartList = new ArrayList<Integer>();
        List<Integer> quoteEndList = new ArrayList<Integer>();
        boolean inQuote = false;
        for (int i = 0; i < content.length(); i++) {
            if (!inQuote && content.charAt(i) == '<') {
                inQuote = true;
                quoteStartList.add(i);
            }
            if (inQuote && content.charAt(i) == '>') {
                inQuote = false;
                quoteEndList.add(i);
            }
        }
        quoteStart = quoteStartList.toArray(new Integer[quoteStartList.size()]);
        quoteEnd = quoteEndList.toArray(new Integer[quoteEndList.size()]);
    }

    private boolean isPositionInQuote(int pos) {
        int startBefore = -Arrays.binarySearch(quoteStart, pos);
        return quoteEnd[startBefore] > pos;
    }

    public List<FindResult> getResults() {
        return results;
    }
}
