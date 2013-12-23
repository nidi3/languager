package stni.languager;

import org.junit.Test;
import stni.languager.crawl.CrawlPattern;
import stni.languager.crawl.FindRegexAction;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static stni.languager.Message.Status.FOUND;

/**
 *
 */
public class KeyExtractorTest extends BaseTest {
    private final File base = fromTestDir("");

    @Test
    public void testSameKey() throws Exception {
        final KeyExtractor extractor = extractFromFile();

        final List<KeyExtractor.FindResultPair> sameKeyResults = extractor.getSameKeyResults();
        assertEquals(1, sameKeyResults.size());
        final KeyExtractor.FindResultPair sameKey = sameKeyResults.get(0);
        assertEquals("key1", extractor.keyOf(sameKey.getResult1()));
        assertEquals("key1", extractor.keyOf(sameKey.getResult2()));
        final SourcePosition pos1 = sameKey.getResult1().getPosition();
        final SourcePosition pos2 = sameKey.getResult2().getPosition();
        assertEquals(new File(base, "test2.html"), pos1.getSource());
        assertEquals(new File(base, "test2.html"), pos2.getSource());
        assertEquals(3, pos1.getLine());
        assertEquals(4, pos2.getLine());
        assertEquals(32, pos1.getColumn());
        assertEquals(15, pos2.getColumn());
    }


    @Test
    public void testSameDefaultValue() throws Exception {
        final KeyExtractor extractor = extractFromFile();

        final List<KeyExtractor.FindResultPair> sameValueResults = extractor.getSameValueResults();
        assertEquals(1, sameValueResults.size());
        final KeyExtractor.FindResultPair sameValue = sameValueResults.get(0);
        assertEquals("key2", extractor.keyOf(sameValue.getResult1()));
        assertEquals("key3", extractor.keyOf(sameValue.getResult2()));
        final SourcePosition pos1 = sameValue.getResult1().getPosition();
        final SourcePosition pos2 = sameValue.getResult2().getPosition();
        assertEquals(new File(base, "test2.html"), pos1.getSource());
        assertEquals(new File(base, "test2.html"), pos2.getSource());
        assertEquals(5, pos1.getLine());
        assertEquals(6, pos2.getLine());
        assertEquals(15, pos1.getColumn());
        assertEquals(9, pos2.getColumn());
    }

    @Test
    public void testUnmessagedTextHtml() throws Exception {
        final KeyExtractor extractor = extractFromFile();
        extractor.extractNegativesFromFiles(
                new CrawlPattern(base, "test2.html", null, "utf-8"),
                ">(.*?)<", null, EnumSet.of(FindRegexAction.Flag.TRIM));

        final Collection<FindResult<List<String>>> negatives = extractor.getNegatives();
        assertEquals(2, negatives.size());
        Iterator<FindResult<List<String>>> iter = negatives.iterator();
        assertEquals("Text9", iter.next().getFinding().get(0));
        assertEquals("{{ignore}}", iter.next().getFinding().get(0));
    }

    @Test
    public void testUnmessagedWithIgnore() throws Exception {
        final KeyExtractor extractor = extractFromFile();
        extractor.extractNegativesFromFiles(
                new CrawlPattern(base, "test2.html", null, "utf-8"),
                ">(.*?)<", "\\{\\{.*?\\}\\}", EnumSet.of(FindRegexAction.Flag.TRIM));

        final Collection<FindResult<List<String>>> negatives = extractor.getNegatives();
        assertEquals(1, negatives.size());
        assertEquals("Text9", negatives.iterator().next().getFinding().get(0));
    }

    @Test
    public void testUnmessagedTextHtmlWithInner() throws Exception {
        final KeyExtractor extractor = new KeyExtractor();
        extractor.extractFromFiles(
                new CrawlPattern(base, "inner.html", null, "utf-8"),
                "<msg key='(.*?)'>(.*?)</msg>", EnumSet.of(FindRegexAction.Flag.WITH_EMPTY));
        extractor.extractNegativesFromFiles(
                new CrawlPattern(base, "inner.html", null, "utf-8"),
                ">(.*?)<", null, EnumSet.of(FindRegexAction.Flag.TRIM));

        final Collection<FindResult<List<String>>> negatives = extractor.getNegatives();
        assertEquals(0, negatives.size());
    }

    @Test
    public void testUnmessagedTextJs() throws Exception {
        final KeyExtractor extractor = new KeyExtractor();
        extractor.extractFromFiles(
                new CrawlPattern(base, "*.js", null, "utf-8"),
                "/\\*-(.*?)\\*/'(.*?)'", EnumSet.of(FindRegexAction.Flag.WITH_EMPTY));
        extractor.extractNegativesFromFiles(
                new CrawlPattern(base, "*.js", null, "utf-8"),
                "'(.*?)'", null, EnumSet.of(FindRegexAction.Flag.TRIM));

        final Collection<FindResult<List<String>>> negatives = extractor.getNegatives();
        assertEquals(1, negatives.size());
        final Iterator<FindResult<List<String>>> iter = negatives.iterator();
        assertEquals("unmessaged", iter.next().getFinding().get(0));

        final SortedMap<String, Message> messages = extractor.getMessages();
        assertEquals(1, messages.size());
        assertEquals(new Message("key", FOUND, "messaged"), messages.get("key"));

        final Set<String> ignoredValues = extractor.getIgnoredValues();
        assertEquals(1, ignoredValues.size());
        assertEquals("ignored", ignoredValues.iterator().next());
    }


    @Test
    public void testMessages() throws Exception {
        final KeyExtractor extractor = extractFromFile();

        assertEquals(3, extractor.getMessages().size());
        final Iterator<Map.Entry<String, Message>> iter = extractor.getMessages().entrySet().iterator();
        assertEntryEquals("key1", new Message("key1", FOUND, "default1"), iter.next());
        assertEntryEquals("key2", new Message("key2", FOUND, "Text2"), iter.next());
        assertEntryEquals("key3", new Message("key3", FOUND, "Text2"), iter.next());
    }

    private KeyExtractor extractFromFile() throws IOException {
        final KeyExtractor extractor = new KeyExtractor();
        extractor.extractFromFiles(
                new CrawlPattern(base, "test2.html", null, "utf-8"),
                "<msg key='(.*?)'>(.*?)</msg>", EnumSet.of(FindRegexAction.Flag.WITH_EMPTY));
        return extractor;
    }

    private void assertEntryEquals(String key, Message value, Map.Entry<String, Message> entry) {
        assertEquals(key, entry.getKey());
        assertEquals(value, entry.getValue());
    }

    @Test
    public void testExtractFromClasspath() throws Exception {
        final KeyExtractor extractor = new KeyExtractor();
        extractor.extractFromClasspath(Arrays.asList("classpath*:org/hibernate/validator/ValidationMessages", "classpath*:**/"));
        final SortedMap<String, Message> messages = extractor.getMessages();
        assertEquals(23, messages.size());
    }

    @Test
    public void testRemoveNewlines() throws Exception {
        final KeyExtractor extractor = new KeyExtractor();
        extractor.getMessages().put("key", new Message("key", FOUND, " a\n\n\n   b "));
        extractor.removeNewlines();
        assertEquals("a b", extractor.getMessages().get("key").getDefaultValue());
    }

}
