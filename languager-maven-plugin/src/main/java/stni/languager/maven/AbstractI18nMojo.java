package stni.languager.maven;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import stni.languager.FindResult;
import stni.languager.SourcePosition;

import java.io.*;
import java.util.List;

/**
 *
 */
public abstract class AbstractI18nMojo extends AbstractMojo {

    private static final int PAD_LEN = 50;

    /**
     * The directory where the searches should start.
     */
    @Parameter(property = "searchBasedir", required = true)
    private File searchBasedir;

    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;

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

    /**
     * Comma separated list of loggers to use. Available loggers are 'console', 'file'.
     */
    @Parameter(property = "log", defaultValue = "console")
    protected String log = "console";

    private DelegatingLogger logger;

    protected void initLogger() throws IOException {
        PrintWriter printWriter = null;
        Log mavenLog = null;
        if (log != null && log.length() > 0) {
            for (String logName : log.split(",")) {
                if ("console".equalsIgnoreCase(logName)) {
                    mavenLog = getLog();
                } else if ("file".equalsIgnoreCase(logName)) {
                    File target = new File(project.getBuild().getDirectory());
                    target.mkdirs();
                    printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(target, "languager.log")), "utf-8"));
                }
            }
        }
        logger = new DelegatingLogger(printWriter, mavenLog);
    }

    protected DelegatingLogger getLogger() {
        return logger;
    }

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

    protected String location(FindResult findResult) {
        final SourcePosition pos = findResult.getPosition();
        return pad() + pos.getSource().getAbsolutePath() + ":[" + pos.getLine() + "," + pos.getColumn() + "]";
    }

    protected String pad() {
        return "                                ";
    }

    protected String pad(String s) {
        if (s.length() >= PAD_LEN) {
            s = s.substring(0, PAD_LEN - 4) + "...'";
        }
        if (s.length() < PAD_LEN) {
            s += "'";
        }
        while (s.length() < PAD_LEN) {
            s += " ";
        }
        return "'" + s;
    }
}
