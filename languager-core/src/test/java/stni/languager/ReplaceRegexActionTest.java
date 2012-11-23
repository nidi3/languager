package stni.languager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;

import org.junit.Test;

/**
 *
 */
public class ReplaceRegexActionTest {
    @Test
    public void simple() throws IOException {
        File base = new File("src/test/resources/stni/languager");
        File target = new File("target");
        new PropertiesWriter(',').write(new File(base, "existing.csv"), "utf-8", target, "msg");

        final Properties de = new Properties();
        de.load(new FileInputStream(new File(target, "msg_de.properties")));

        final Properties en = new Properties();
        en.load(new FileInputStream(new File(target, "msg_en.properties")));

        FileCrawler<ReplaceCrawlPattern, ReplaceRegexAction> crawler = FileCrawler.create(base, new ReplaceRegexAction());
        crawler.addCrawlPattern(new ReplaceCrawlPattern("<msg key='(.*?)'>(.*?)</msg>", "*.html", null, "utf-8", new File(target, "de"), new ReplaceCrawlPattern.Replacer() {
            public String replace(Matcher m) {
                String s = de.getProperty(m.group(1));
                return s == null ? "" : s;
            }
        }));
        crawler.addCrawlPattern(new ReplaceCrawlPattern("<msg key='(.*?)'>(.*?)</msg>", "*.html", null, "utf-8", new File(target, "en"), "($1)", en));

        crawler.crawl();
    }
}
