package stni.languager.maven;

import org.apache.maven.plugin.MojoExecutionException;
import stni.languager.Util;
import stni.languager.crawl.CrawlAction;
import stni.languager.crawl.CrawlPattern;
import stni.languager.crawl.FileCrawler;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

/**
 * @author stni
 */
public abstract class AbstractOutputMojo extends AbstractI18nMojo {
    protected static final String PROPERTIES = ".properties";

    /**
     * @parameter expression="${replacedDirectory}" default-value="target/${project.build.finalName}"
     */
    protected File replacedDirectory;

    /**
     * @parameter expression="${searches}"
     */
    protected List<ReplaceSearch> searches;

    /**
     * @parameter expression="${baseName}"
     * @required
     */
    protected String baseName;

    /**
     * @parameter expression="${propertiesDirectory}" default-value="target/generated-sources"
     */
    protected File propertiesDirectory;

    protected void writePerLanguage(boolean loadProperties) throws MojoExecutionException {
        try {
            List<File> props = Util.getFiles(propertiesDirectory, baseName + "_*" + PROPERTIES, null);
            for (File prop : props) {
                int pos = prop.getName().indexOf("_");
                String lang = prop.getName().substring(pos + 1, prop.getName().length() - PROPERTIES.length());

                Properties p = null;
                if (loadProperties) {
                    p = new Properties();
                    p.load(new FileInputStream(prop));
                }

                for (ReplaceSearch search : searches) {
                    final File targetDir = new File(replacedDirectory, lang);
                    FileCrawler crawler = new FileCrawler(
                            new CrawlPattern(new File(searchBasedir().getAbsolutePath().replace("$lang", lang)), search.getIncludes(), search.getExcludes(), search.getEncoding()));
                    crawler.crawl(doPerLanguage(search, p, targetDir));
                }
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Problem writing output", e);
        }
    }

    protected abstract CrawlAction doPerLanguage(ReplaceSearch search, Properties p, File targetDir);
}