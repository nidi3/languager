package stni.languager;

import stni.languager.crawl.CrawlPattern;
import stni.languager.crawl.FileCrawler;
import stni.languager.crawl.FindRegexAction;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static stni.languager.Message.Status.FOUND;

/**
 *
 */
public class KeyExtractor {
    private static final Comparator<FindResult<?>> FIND_RESULT_SORTER = new Comparator<FindResult<?>>() {
        public int compare(FindResult<?> result1, FindResult<?> result2) {
            int res = result1.getPosition().getSource().compareTo(result2.getPosition().getSource());
            if (res == 0) {
                res = result1.getPosition().getLine() - result2.getPosition().getLine();
            }
            return res;
        }
    };

    public static class FindResultPair {
        private final FindResult<List<String>> result1;
        private final FindResult<List<String>> result2;

        FindResultPair(FindResult<List<String>> result1, FindResult<List<String>> result2) {
            this.result1 = result1;
            this.result2 = result2;
        }

        public FindResult<List<String>> getResult1() {
            return result1;
        }

        public FindResult<List<String>> getResult2() {
            return result2;
        }
    }

    private final SortedMap<String, Message> messages = new TreeMap<String, Message>();
    private final Set<String> ignoredValues = new HashSet<String>();
    private final Map<String, FindResult<List<String>>> negatives = new HashMap<String, FindResult<List<String>>>();
    private final Map<File, List<FindResult<List<String>>>> resultsByLocation = new HashMap<File, List<FindResult<List<String>>>>();
    private final Map<String, FindResult<List<String>>> resultsByKey = new HashMap<String, FindResult<List<String>>>();
    private final Map<String, FindResult<List<String>>> resultsByValue = new HashMap<String, FindResult<List<String>>>();
    private final List<FindResultPair> sameKeyResults = new ArrayList<FindResultPair>();
    private final List<FindResultPair> sameValueResults = new ArrayList<FindResultPair>();
    private boolean cleanedNegatives = true;

    public void extractFromFiles(CrawlPattern crawlPattern, String regex, EnumSet<FindRegexAction.Flag> flags) throws IOException {
        cleanedNegatives = false;
        FileCrawler crawler = createCrawler(crawlPattern);
        for (FindResult result : crawler.crawl(new FindRegexAction(regex, null, flags)).getResults()) {
            final String key = keyOf(result);
            if (key.length() == 0) {
                ignoredValues.add(valueOf(result));
            } else {
                checkSameKey(result);
                checkSameValue(result);
                Message message = messages.get(key);
                if (message == null) {
                    message = new Message(key, FOUND, valueOf(result));
                }
                message.addOccurrence(result.getPosition());
                messages.put(key, message);
                saveResultByLocation(result);
            }
        }
    }

    private void saveResultByLocation(FindResult result) {
        List<FindResult<List<String>>> resultListByLocation = resultsByLocation.get(result.getPosition().getSource());
        if (resultListByLocation == null) {
            resultListByLocation = new ArrayList<FindResult<List<String>>>();
            resultsByLocation.put(result.getPosition().getSource(), resultListByLocation);
        }
        resultListByLocation.add(result);
    }

    public void extractNegativesFromFiles(CrawlPattern crawlPattern, String regex, String ignoreRegex, EnumSet<FindRegexAction.Flag> flags) throws IOException {
        cleanedNegatives = false;
        FileCrawler crawler = createCrawler(crawlPattern);
        for (FindResult result : crawler.crawl(new FindRegexAction(regex, ignoreRegex, flags)).getResults()) {
            negatives.put(keyOf(result), result);
        }
    }

    protected FileCrawler createCrawler(CrawlPattern crawlPattern) {
        return new FileCrawler(crawlPattern);
    }

    private void checkSameKey(FindResult<List<String>> result) {
        String value = valueOf(result);
        String key = keyOf(result);
        final FindResult sameKey = resultsByKey.get(key);
        if (sameKey != null && !nullSafeEquals(value, valueOf(sameKey))) {
            sameKeyResults.add(new FindResultPair(sameKey, result));
        }
        resultsByKey.put(key, result);
    }

    private void checkSameValue(FindResult result) {
        String value = valueOf(result);
        String key = keyOf(result);
        final FindResult sameValue = resultsByValue.get(value);
        if (sameValue != null && !key.equals(keyOf(sameValue))) {
            sameValueResults.add(new FindResultPair(sameValue, result));
        }
        resultsByValue.put(value, result);
    }

    public List<FindResultPair> getSameKeyResults() {
        return sameKeyResults;
    }

    public List<FindResultPair> getSameValueResults() {
        return sameValueResults;
    }

    public Collection<FindResult<List<String>>> getNegatives() {
        cleanNegatives();
        final List<FindResult<List<String>>> findResults = new ArrayList<FindResult<List<String>>>(negatives.values());
        Collections.sort(findResults, FIND_RESULT_SORTER);
        return findResults;
    }

    private void cleanNegatives() {
        if (!cleanedNegatives) {
            cleanedNegatives = true;
            removeInnerNegatives();
            removeIgnoredNegatives();
        }
    }

    private void removeInnerNegatives() {
        for (Iterator<FindResult<List<String>>> iter = negatives.values().iterator(); iter.hasNext(); ) {
            final FindResult result = iter.next();
            final SourcePosition pos = result.getPosition();
            final List<FindResult<List<String>>> sourceResults = resultsByLocation.get(pos.getSource());
            if (sourceResults != null) {
                for (FindResult sourceResult : sourceResults) {
                    if (sourceResult.getPosition().getStart() > pos.getStart()) {
                        break;
                    } else if (sourceResult.getPosition().getEnd() >= pos.getEnd()) {
                        iter.remove();
                        break;
                    }
                }
            }
        }
    }

    private void removeIgnoredNegatives() {
        for (String ignored : ignoredValues) {
            negatives.remove(ignored);
        }
    }

    public SortedMap<String, Message> getMessages() {
        return messages;
    }

    public Set<String> getIgnoredValues() {
        return ignoredValues;
    }

    public String location(FindResult result) {
        final SourcePosition pos = result.getPosition();
        return pos.getSource() + ":" + pos.getLine() + ":" + pos.getColumn();
    }

    public String valueOf(FindResult<List<String>> result) {
        return result.getFinding().size() > 1 ? result.getFinding().get(1) : null;
    }

    public String keyOf(FindResult<List<String>> result) {
        return result.getFinding().get(0);
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

        final OccurrenceWriter occurrenceWriter = new OccurrenceWriter();
        occurrenceWriter.write(file, messages.values());
    }
}
