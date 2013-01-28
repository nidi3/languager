package org.languager.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;

import stni.languager.FindResult;
import stni.languager.SourcePosition;

/**
 * @phase generate-resources
 */
public abstract class AbstractI18nMojo extends AbstractMojo {

    private static final int PAD_LEN = 50;

    /**
     * @parameter expression="${searchBasedir}"
     * @required
     * @readonly
     */
    private File searchBasedir;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @parameter expression="${csvFile}" default-value="src/main/resources/messages.csv"
     */
    private File csvFile;

    /**
     * @parameter expression="${csvEncoding}" default-value="utf-8"
     */
    protected String csvEncoding;

    /**
     * @parameter expression="${csvSeparator}" default-value=';'
     */
    protected char csvSeparator = ';';

    /**
     * @parameter expression="${log}" default-value="console"
     */
    protected String log = "console";

    /**
     * @parameter expression="${customizerClass}"
     */
    protected String customizerClass;

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
