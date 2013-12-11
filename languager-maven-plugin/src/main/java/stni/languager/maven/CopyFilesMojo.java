package stni.languager.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import stni.languager.crawl.CopyAction;
import stni.languager.crawl.CrawlAction;

import java.io.File;
import java.util.Properties;

/**
 * @author stni
 * @goal copyFiles
 */
public class CopyFilesMojo extends AbstractOutputMojo {
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Start copying files");
        writePerLangauge(false);
    }

    @Override
    protected CrawlAction doPerLanguage(ReplaceSearch search, Properties p, File targetDir) {
        return new CopyAction(targetDir);
    }
}