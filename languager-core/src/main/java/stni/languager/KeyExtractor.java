package stni.languager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
public class KeyExtractor {
    public static class FindResultPair {
        private final FindResult result1;
        private final FindResult result2;

        FindResultPair(FindResult result1, FindResult result2) {
            this.result1 = result1;
            this.result2 = result2;
        }

        public FindResult getResult1() {
            return result1;
        }

        public FindResult getResult2() {
            return result2;
        }
    }

    private final SortedMap<String, Message> messages = new TreeMap<String, Message>();
    private final Map<String, FindResult> resultsByKey = new HashMap<String, FindResult>();
    private final Map<String, FindResult> resultsByDefaultValue = new HashMap<String, FindResult>();
    private final List<FindResultPair> sameKeyResults = new ArrayList<FindResultPair>();
    private final List<FindResultPair> sameDefaultValueResults = new ArrayList<FindResultPair>();

    public void extractFromFiles(File basedir, List<CrawlPattern> searchPaths) throws IOException {
        FileCrawler<FindRegexAction> crawler = initCrawler(basedir, searchPaths);

        for (FindResult result : crawler.crawl().getResults()) {
            checkSameKey(result);
            checkSameDefaultValue(result);
            messages.put(keyOf(result), new Message(keyOf(result), true, defaultValueOf(result)));
        }
    }

    protected FileCrawler<FindRegexAction> initCrawler(File basedir, List<CrawlPattern> searchPaths) {
        FileCrawler<FindRegexAction> crawler = FileCrawler.create(basedir, new FindRegexAction(null,true));
        for (CrawlPattern searchPath : searchPaths) {
            if (searchPath.getEncoding() == null) {
                searchPath.setEncoding("utf-8");
            }
            crawler.addCrawlPattern(searchPath);
        }
        return crawler;
    }

    private void checkSameKey(FindResult result) {
        String defaultValue = defaultValueOf(result);
        String key = keyOf(result);
        final FindResult sameKey = resultsByKey.get(key);
        if (sameKey != null && !nullSafeEquals(defaultValue, defaultValueOf(sameKey))) {
            sameKeyResults.add(new FindResultPair(result, sameKey));
        }
        resultsByKey.put(key, result);
    }

    private void checkSameDefaultValue(FindResult result) {
        String defaultValue = defaultValueOf(result);
        String key = keyOf(result);
        final FindResult sameDefaultValue = resultsByDefaultValue.get(defaultValue);
        if (sameDefaultValue != null && !key.equals(keyOf(sameDefaultValue))) {
            sameDefaultValueResults.add(new FindResultPair(result, sameDefaultValue));
        }
        resultsByDefaultValue.put(defaultValue, result);
    }

    public List<FindResultPair> getSameKeyResults() {
        return sameKeyResults;
    }

    public List<FindResultPair> getSameDefaultValueResults() {
        return sameDefaultValueResults;
    }

    public String location(FindResult result) {
        return result.getSource() + ":" + result.getLine() + ":" + result.getColumn();
    }

    public String defaultValueOf(FindResult result) {
        return result.getFindings().size() > 1 ? result.getFindings().get(1) : null;
    }

    public String keyOf(FindResult result) {
        return result.getFindings().get(0);
    }

    private boolean nullSafeEquals(String a, String b) {
        return a == b || (a != null && a.equals(b));
    }

    public void extractFromClasspath(List<String> propertyLocations) throws IOException {
        PropertiesFinder finder = new PropertiesFinder();
        for (String propertyLocation : propertyLocations) {
            finder.addPropertyLocation(propertyLocation);
        }
        messages.putAll(finder.findProperties());
    }


    public void removeNewlines() {
        for (Map.Entry<String, Message> message : messages.entrySet()) {
            messages.put(message.getKey(), message.getValue().transformed(new NewlineRemover()));
        }
    }

    public void writeCsv(File file, String encoding, char separator) throws IOException {
        file.getParentFile().mkdirs();

        MessagesWriter writer = new MessagesWriter(encoding, separator);
        writer.write(file, messages);
    }
}
