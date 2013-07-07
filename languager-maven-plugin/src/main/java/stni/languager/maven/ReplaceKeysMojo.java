package stni.languager.maven;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

import stni.languager.crawl.CrawlAction;
import stni.languager.crawl.CrawlPattern;
import stni.languager.crawl.FileCrawler;
import stni.languager.crawl.ReplacePropertiesAction;
import stni.languager.crawl.ReplaceRegexAction;
import stni.languager.crawl.ReplaceRegexActionParameter;

/**
 * @author stni
 * @goal replaceKeys
 * @requiresDependencyResolution compile
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
        if (customizerClass != null) {
            getLog().info("Running customizer " + customizerClass);
            try {
                extendPluginClasspath(project.getCompileClasspathElements());
                final Class<?> customizer = Class.forName(customizerClass.replace('/', '.'), true, Thread.currentThread().getContextClassLoader());
                final Method main = customizer.getMethod("main", new Class[]{new String[0].getClass()});
                main.invoke(null, (Object) new String[]{project.getBasedir().getAbsolutePath()});
            } catch (Exception e) {
                throw new MojoExecutionException("Problem running customizer", e);
            }
        }
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
                            new CrawlPattern(new File(searchBasedir().getAbsolutePath().replace("$lang", lang)), search.getIncludes(), search.getExcludes(), search.getEncoding()));
                    final File targetDir = new File(replacedDirectory, lang);
                    CrawlAction action;
                    if (search.getRegex() == null) {
                        action = new ReplacePropertiesAction(p, targetDir);
                    } else {
                        ReplaceRegexActionParameter actionParameter = new ReplaceRegexActionParameter(
                                targetDir, search.getReplacement(), search.getParameterMarker(), search.getParameterSeparator(), p, search.getEscapes());
                        action = new ReplaceRegexAction(search.getRegex(), null, actionParameter);
                    }
                    crawler.crawl(action);
                }
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Problem replacing keys", e);
        }
    }
}