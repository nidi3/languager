package guru.nidi.languager.maven;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;

import java.io.File;
import java.util.List;

/**
 *
 */
public abstract class AbstractI18nMojo extends AbstractLoggingMojo {
    /**
     * The directory where the searches should start.
     */
    @Parameter(property = "searchBasedir", required = true)
    private File searchBasedir;

    /**
     * The translations csv file to write.
     */
    @Parameter(property = "csvFile", defaultValue = "src/main/resources/messages.csv")
    private File csvFile;

    /**
     * The encoding of the translations csv file.
     */
    @Parameter(property = "csvEncoding", defaultValue = "UTF-8")
    protected String csvEncoding;

    /**
     * The field separator to use in the translations csv file.
     */
    @Parameter(property = "csvSeparator", defaultValue = ";")
    protected char csvSeparator = ';';

    protected File searchBasedir() {
        return searchBasedir != null ? searchBasedir : project.getBasedir();
    }

    protected File getCsvFile() {
        if (csvFile == null) {
            return new File(project.getBasedir(), "src/main/resources/messages.csv");
        }
        return csvFile;
    }

    protected void extendPluginClasspath(List<String> elements) throws MojoExecutionException {
        ClassWorld world = new ClassWorld();
        try {
            ClassRealm realm = world.newRealm("maven", Thread.currentThread().getContextClassLoader());
            for (String element : elements) {
                File elementFile = new File(element);
                getLog().debug("*** Adding element to plugin classpath " + elementFile.getPath());
                realm.addConstituent(elementFile.toURI().toURL());
            }
            Thread.currentThread().setContextClassLoader(realm.getClassLoader());
        } catch (Exception ex) {
            throw new MojoExecutionException(ex.toString(), ex);
        }
    }

    protected List<String> compileClasspath() throws DependencyResolutionRequiredException {
        @SuppressWarnings("unchecked")
        final List<String> compileClasspathElements = project.getCompileClasspathElements();
        return compileClasspathElements;
    }

    protected void extendClasspathWithCompile() throws DependencyResolutionRequiredException, MojoExecutionException {
        extendPluginClasspath(compileClasspath());
    }
}
