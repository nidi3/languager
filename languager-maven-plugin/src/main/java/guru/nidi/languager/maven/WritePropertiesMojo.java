package guru.nidi.languager.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import guru.nidi.languager.PropertiesWriter;

import java.io.File;
import java.io.IOException;

/**
 * Write a set of properties files out of a csv file containing keys and translations.
 *
 * @author stni
 */
@Mojo(name = "writeProperties", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class WritePropertiesMojo extends AbstractI18nMojo {
    /**
     * The directory to write the properties files to.
     */
    @Parameter(property = "propertiesDirectory", defaultValue = "target/generated-sources")
    protected File propertiesDirectory;

    /**
     * The basename of the properties to be written.
     */
    @Parameter(property = "baseName", required = true)
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
