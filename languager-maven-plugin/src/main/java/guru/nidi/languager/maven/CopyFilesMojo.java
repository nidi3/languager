package guru.nidi.languager.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import guru.nidi.languager.crawl.CopyAction;
import guru.nidi.languager.crawl.CrawlAction;

import java.io.File;
import java.util.Properties;

/**
 * Copy files into all translated directories.
 *
 * @author stni
 */
@Mojo(name = "copyFiles", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class CopyFilesMojo extends AbstractOutputMojo {
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Start copying files");
        writePerLanguage(false);
    }

    @Override
    protected CrawlAction doPerLanguage(ReplaceSearch search, Properties p, File targetDir) {
        return new CopyAction(targetDir);
    }
}