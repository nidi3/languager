package org.languager.maven;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import stni.languager.PropertiesWriter;

/**
 * @author stni
 * @goal writeProperties
 */
public class WritePropertiesMojo extends AbstractI18nMojo {
    /**
     * @parameter expression="${propertiesDirectory}" default-value="target/generated-sources"
     */
    protected File propertiesDirectory;

    /**
     * @parameter expression="${baseName}"
     * @required
     */
    protected String baseName;


    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Start writing properties files");
        try {
            PropertiesWriter writer = new PropertiesWriter(csvSeparator);
            if (propertiesDirectory == null) {
                propertiesDirectory = new File(project.getBasedir(), "target/generated-sources");
            }
            propertiesDirectory.mkdirs();
            writer.write(getCsvFile(), csvEncoding, propertiesDirectory, baseName);
        } catch (IOException e) {
            throw new MojoExecutionException("Problem writing properties files", e);
        }
    }
}
