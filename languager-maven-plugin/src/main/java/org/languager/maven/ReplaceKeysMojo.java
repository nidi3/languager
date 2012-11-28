package org.languager.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

import stni.languager.crawl.CrawlPattern;
import stni.languager.crawl.FileCrawler;
import stni.languager.crawl.ReplaceRegexAction;
import stni.languager.crawl.ReplaceRegexActionParameter;

/**
 * @author stni
 * @goal replaceKeys
 */
public class ReplaceKeysMojo extends AbstractI18nMojo {
    private static final String PROPERTIES = ".properties";

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


    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Start replacing keys");
        try {
            List<File> props = FileUtils.getFiles(propertiesDirectory, baseName + "_*" + PROPERTIES, null);
            for (File prop : props) {
                int pos = prop.getName().indexOf("_");
                String lang = prop.getName().substring(pos + 1, prop.getName().length() - PROPERTIES.length());
                Properties p = new Properties();
                p.load(new FileInputStream(prop));
                for (ReplaceSearch search : searches) {
                    FileCrawler crawler = new FileCrawler(
                            new CrawlPattern(searchBasedir(), search.getIncludes(), search.getExcludes(), search.getEncoding()));
                    ReplaceRegexActionParameter actionParameter = new ReplaceRegexActionParameter(
                            new File(replacedDirectory, lang), search.getReplacement(), p, search.getEscapes());
                    crawler.crawl(new ReplaceRegexAction(search.getRegex(), null, actionParameter));
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Problem replacing keys", e);
        }
    }
}