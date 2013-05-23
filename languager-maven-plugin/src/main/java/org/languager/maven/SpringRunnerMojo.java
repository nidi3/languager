package org.languager.maven;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;

/**
 * @goal runSpring
 * @requiresDependencyResolution compile
 */
public class SpringRunnerMojo extends AbstractI18nMojo {
    /**
     * @parameter expression="${contextFile}"
     * @required
     */
    protected File contextFile;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("******************** If using IntelliJ, consider using grep console plugin ********************");
        LogConfiguration.useLogConfig("logback-blue.xml");

        try {
            extendPluginClasspath(project.getCompileClasspathElements());

            final FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("file:" + contextFile.getAbsolutePath());
            context.start();
            getLog().info("Started. Stopping spring context...");
            context.stop();
            getLog().info("Stopped.");
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Problem", e);
        }
        LogConfiguration.useLogConfig("logback.xml");
    }
}
