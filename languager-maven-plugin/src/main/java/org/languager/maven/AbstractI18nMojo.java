package org.languager.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * @phase generate-sources
 */
public abstract class AbstractI18nMojo extends AbstractMojo {
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
}
