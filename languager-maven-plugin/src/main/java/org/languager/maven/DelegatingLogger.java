package org.languager.maven;

import java.io.PrintWriter;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: stni
 * Date: 11.12.12
 * Time: 13:35
 * To change this template use File | Settings | File Templates.
 */
public class DelegatingLogger {
    private List<PrintWriter> delegates;

    public DelegatingLogger(List<PrintWriter> delegates) {
        this.delegates = delegates;
    }

    public void log(String message) {
        for (PrintWriter pw : delegates) {
            pw.println(message);
        }
    }

    public boolean isEmpty() {
        return delegates.isEmpty();
    }

    public void close() {
        for (PrintWriter pw : delegates) {
            pw.close();
        }
    }
}
