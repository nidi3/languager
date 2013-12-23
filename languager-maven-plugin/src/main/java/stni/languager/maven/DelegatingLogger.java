package stni.languager.maven;

import org.apache.maven.plugin.logging.Log;
import stni.languager.Logger;

import java.io.PrintWriter;

/**
 *
 */
public class DelegatingLogger implements Logger {
    private final PrintWriter printWriter;
    private final Log log;

    public DelegatingLogger(PrintWriter printWriter, Log log) {
        this.printWriter = printWriter;
        this.log = log;
    }

    @Override
    public void log(String message) {
        if (printWriter != null) {
            printWriter.println(message);
        }
        if (log != null) {
            log.info(message);
        }
    }

    @Override
    public void logSection(String message) {
        log("");
        log("******************************* " + message);
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
