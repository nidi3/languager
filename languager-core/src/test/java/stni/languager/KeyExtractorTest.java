package stni.languager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.junit.Test;

import stni.languager.crawl.CrawlPattern;
import stni.languager.crawl.FindRegexAction;
import stni.languager.crawl.FindResult;

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
        assertEquals(new File(base, "test2.html").getAbsolutePath(), sameKey.getResult1().getSource());
        assertEquals(new File(base, "test2.html").getAbsolutePath(), sameKey.getResult2().getSource());
        assertEquals(3, sameKey.getResult1().getLine());
        assertEquals(4, sameKey.getResult2().getLine());
        assertEquals(15, sameKey.getResult1().getColumn());
        assertEquals(15, sameKey.getResult2().getColumn());
    }


    @Test
    public void testSameDefaultValue() throws Exception {
        final KeyExtractor extractor = extractFromFile();

        final List<KeyExtractor.FindResultPair> sameValueResults = extractor.getSameValueResults();
        assertEquals(1, sameValueResults.size());
        final KeyExtractor.FindResultPair sameValue = sameValueResults.get(0);
        assertEquals("key2", extractor.keyOf(sameValue.getResult1()));
        assertEquals("key3", extractor.keyOf(sameValue.getResult2()));
        assertEquals(new File(base, "test2.html").getAbsolutePath(), sameValue.getResult1().getSource());
        assertEquals(new File(base, "test2.html").getAbsolutePath(), sameValue.getResult2().getSource());
        assertEquals(5, sameValue.getResult1().getLine());
        assertEquals(6, sameValue.getResult2().getLine());
        assertEquals(9, sameValue.getResult1().getColumn());
        assertEquals(9, sameValue.getResult2().getColumn());
    }

    @Test
    public void testUnmessagedText1() throws Exception {
        final KeyExtractor extractor = extractFromFile();
        extractor.extractNegativesFromFiles(
                new CrawlPattern(base, "test2.html", null, "utf-8"),
                ">(.*?)<", EnumSet.of(FindRegexAction.Flag.TRIM));

        final Collection<FindResult> negatives = extractor.getNegatives();
        assertEquals(2, negatives.size());
        final Iterator<FindResult> iter = negatives.iterator();
        final String first = iter.next().getFindings().get(0);
        assertTrue(first.equals("default1") || first.equals("Text1"));
        final String second = iter.next().getFindings().get(0);
        assertTrue(second.equals("default1") || second.equals("Text1"));
    }

    @Test
    public void testUnmessagedText2() throws Exception {
        final KeyExtractor extractor = new KeyExtractor();
        extractor.extractFromFiles(
                new CrawlPattern(base, "*.js", null, "utf-8"),
                "/\\*-(.*?)\\*/'(.*?)'", null);
        extractor.extractNegativesFromFiles(
                new CrawlPattern(base, "*.js", null, "utf-8"),
                "'(.*?)'", EnumSet.of(FindRegexAction.Flag.TRIM));

        final Collection<FindResult> negatives = extractor.getNegatives();
        assertEquals(1, negatives.size());
        final Iterator<FindResult> iter = negatives.iterator();
        assertEquals("unmessaged", iter.next().getFindings().get(0));

        final SortedMap<String, Message> messages = extractor.getMessages();
        assertEquals(1, messages.size());
        assertEquals(new Message("key", true, "messaged"), messages.get("key"));
    }

    @Test
    public void testMessages() throws Exception {
        final KeyExtractor extractor = extractFromFile();

        assertEquals(3, extractor.getMessages().size());
        final Iterator<Map.Entry<String, Message>> iter = extractor.getMessages().entrySet().iterator();
        assertEntryEquals("key1", new Message("key1", true, "default2"), iter.next());
        assertEntryEquals("key2", new Message("key2", true, "Text2"), iter.next());
        assertEntryEquals("key3", new Message("key3", true, "Text2"), iter.next());
    }

    private KeyExtractor extractFromFile() throws IOException {
        final KeyExtractor extractor = new KeyExtractor();
        extractor.extractFromFiles(
                new CrawlPattern(base, "test2.html", null, "utf-8"),
                "<msg key='(.*?)'>(.*?)</msg>", null);
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
        extractor.getMessages().put("key", new Message("key", true, " a\n\n\n   b "));
        extractor.removeNewlines();
        assertEquals("a b", extractor.getMessages().get("key").getDefaultValue());
    }

}
