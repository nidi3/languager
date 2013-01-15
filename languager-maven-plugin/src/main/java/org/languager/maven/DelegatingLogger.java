package org.languager.maven;

import java.io.PrintWriter;

import org.apache.maven.plugin.logging.Log;

/**
 *
 */
public class DelegatingLogger {
    private final PrintWriter printWriter;
    private final Log log;

    public DelegatingLogger(PrintWriter printWriter, Log log) {
        this.printWriter = printWriter;
        this.log = log;
    }

    public void log(String message) {
        if (printWriter != null) {
            printWriter.println(message);
        }
        if (log != null) {
            log.info(message);
        }
    }

    public void logSection(String message) {
        log("****************** " + message);
    }

    public boolean isEmpty() {
        return printWriter == null && log == null;
    }

    public void close() {
        if (printWriter != null) {
            printWriter.close();
        }
    }
}
