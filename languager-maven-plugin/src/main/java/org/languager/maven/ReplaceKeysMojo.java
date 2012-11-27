package org.languager.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

import stni.languager.FileCrawler;
import stni.languager.ReplaceRegexAction;
import stni.languager.ReplaceRegexActionParameter;

/**
 * @author stni
 * @goal replaceKeys
 */
public class ReplaceKeysMojo extends AbstractI18nMojo {
    /**
     * @parameter expression="${basedir}"
     * @required
     */
    protected File basedir;

    /**
     * @parameter expression="${replacedDirectory}" default-value="target/${project.build.finalName}"
     */
    protected File replacedDirectory;

    /**
     * @parameter expression="${searchPaths}"
     */
    protected List<ReplaceRegexActionParameter> searchPaths;

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
            FileCrawler<ReplaceRegexAction> crawler = FileCrawler.create(basedir, new ReplaceRegexAction(null, true, null));
            List<File> props = FileUtils.getFiles(propertiesDirectory, baseName + "_*.properties", null);
            for (File prop : props) {
                int pos = prop.getName().indexOf("_");
                String lang = prop.getName().substring(pos + 1, prop.getName().length() - 11);
                Properties p = new Properties();
                p.load(new FileInputStream(prop));
//                for (ReplaceRegexActionParameter searchPath : searchPaths) {
//                    if (searchPath.getEncoding() == null) {
//                        searchPath.setEncoding("utf-8");
//                    }
//                    ReplaceRegexActionParameter actionParameter = new ReplaceRegexActionParameter(searchPath.getRegex(), false, searchPath.getIncludes(), searchPath.getExcludes(), searchPath.getEncoding(),
//                            new File(replacedDirectory, lang), searchPath.getReplacement(), p);
//                    actionParameter.setEscapes(searchPath.getEscapes());
//                    crawler.addCrawlPattern(actionParameter);
//                }
            }
            crawler.crawl();
        } catch (IOException e) {
            throw new MojoExecutionException("Problem replaceing keys", e);
        }
    }
}