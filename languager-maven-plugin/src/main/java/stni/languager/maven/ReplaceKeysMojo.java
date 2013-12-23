package stni.languager.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import stni.languager.crawl.CrawlAction;
import stni.languager.crawl.ReplacePropertiesAction;
import stni.languager.crawl.ReplaceRegexAction;
import stni.languager.crawl.ReplaceRegexActionParameter;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author stni
 * @goal replaceKeys
 * @requiresDependencyResolution compile
 */
public class ReplaceKeysMojo extends AbstractOutputMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (customizerClass != null) {
            getLog().info("Running customizer " + customizerClass);
            try {
                extendClasspathWithCompile();
                final Class<?> customizer = Class.forName(customizerClass.replace('/', '.'), true, Thread.currentThread().getContextClassLoader());
                final Method main = customizer.getMethod("main", new Class[]{String[].class});
                main.invoke(null, (Object) new String[]{project.getBasedir().getAbsolutePath()});
            } catch (Exception e) {
                throw new MojoExecutionException("Problem running customizer", e);
            }
        }

        getLog().info("Start replacing keys");
        writePerLanguage(true);
    }

    protected CrawlAction doPerLanguage(ReplaceSearch search, Properties p, File targetDir) {
        if (search.getRegex() == null) {
            return new ReplacePropertiesAction(p, targetDir);
        }
        ReplaceRegexActionParameter actionParameter = new ReplaceRegexActionParameter(
                targetDir, search.getReplacement(), search.getParameterMarker(), search.getParameterSeparator(), p, search.getEscapes());
        return new ReplaceRegexAction(search.getRegex(), null, actionParameter);
    }

}