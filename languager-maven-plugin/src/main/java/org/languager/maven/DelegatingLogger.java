package org.languager.maven;

import java.io.PrintWriter;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

/**
 * Created with IntelliJ IDEA.
 * User: stni
 * Date: 11.12.12
 * Time: 13:35
 * To change this template use File | Settings | File Templates.
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

    public boolean isEmpty() {
        return printWriter == null && log == null;
    }

    public void close() {
        if (printWriter != null) {
            printWriter.close();
        }
    }
}
