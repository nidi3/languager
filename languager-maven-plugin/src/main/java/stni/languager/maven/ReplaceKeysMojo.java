package stni.languager.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import stni.languager.crawl.CrawlAction;
import stni.languager.crawl.ReplacePropertiesAction;
import stni.languager.crawl.ReplaceRegexAction;
import stni.languager.crawl.ReplaceRegexActionParameter;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Replace all keys matching a regex by their respective translations. This is done for every available language.
 *
 * @author stni
 */
@Mojo(name = "replaceKeys", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class ReplaceKeysMojo extends AbstractOutputMojo {
    /**
     * The name of a class in the classpath. Its main method will be invoked before the replacement takes place.
     */
    @Parameter(property = "customizerClass")
    protected String customizerClass;

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