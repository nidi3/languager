package stni.languager.crawl;

import org.junit.Assert;
import org.junit.Test;
import stni.languager.BaseTest;
import stni.languager.PropertiesWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 *
 */
public class ReplaceRegexActionTest extends BaseTest {
    @Test
    public void simple() throws IOException {
        File base = fromTestDir("");
        File target = fromBaseDir("target");
        new PropertiesWriter(',').write(new File(base, "existing.csv"), "utf-8", target, "msg");

        final Properties de = new Properties();
        de.load(new FileInputStream(new File(target, "msg_de.properties")));

        final Properties en = new Properties();
        en.load(new FileInputStream(new File(target, "msg_en.properties")));

        FileCrawler crawler = new FileCrawler(new CrawlPattern(base, "*.html", null, "utf-8"));
        crawler.crawl(
                new ReplaceRegexAction("<msg key='(.*?)'>(.*?)</msg>", null,
                        new ReplaceRegexActionParameter(new File(target, "de"), new ReplaceRegexActionParameter.Replacer() {
                            public String replace(Matcher m) {
                                String s = de.getProperty(m.group(1));
                                return s == null ? "" : s;
                            }
                        })
                )
        );
        assertFileEquals(new File(base, "test_expected_de.html"), new File(target, "de/test.html"));

        crawler.crawl(
                new ReplaceRegexAction("<msg key='(.*?)'>(.*?)</msg>", null,
                        new ReplaceRegexActionParameter(new File(target, "en"), "($1)", null, null, en, null)
                )
        );
        assertFileEquals(new File(base, "test_expected_en.html"), new File(target, "en/test.html"));
    }

    @Test
    public void parameterized() throws IOException {
        File base = fromTestDir("");
        File target = fromBaseDir("target");
        Properties properties = new Properties();
        properties.setProperty("key", "1{}2{}3{}4");
        FileCrawler crawler = new FileCrawler(new CrawlPattern(base, "parameter.html", null, "utf-8"));
        crawler.crawl(
                new ReplaceRegexAction("<msg key='(.*?)'(?:\\s*params='(.*?)')?>(.*?)</msg>", null,
                        new ReplaceRegexActionParameter(new File(target, "de"), "$1", "{}", ",", properties, null)
                )
        );
        assertFileEquals(new File(base, "test_expected_parameter.html"), new File(target, "de/parameter.html"));
    }

    private void assertFileEquals(File expected, File toTest) throws IOException {
        if (!fileContent(expected).equals(fileContent(toTest))) {
            Assert.fail("File " + toTest + " is not equal to file " + expected);
        }
    }

    private String fileContent(File file) throws IOException {
        InputStreamReader in = new InputStreamReader(new FileInputStream(file), "utf-8");
        char[] ch = new char[(int) file.length()];
        int read = in.read(ch);
        in.close();
        return new String(ch, 0, read);
    }
}
