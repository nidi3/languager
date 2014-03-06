package guru.nidi.languager.maven;

import guru.nidi.languager.FindResult;
import guru.nidi.languager.SourcePosition;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;

/**
 *
 */
public abstract class AbstractLoggingMojo extends AbstractMojo {

    private static final int PAD_LEN = 50;

    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;

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
