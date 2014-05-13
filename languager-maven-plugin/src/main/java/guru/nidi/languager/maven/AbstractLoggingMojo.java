/*
 * Copyright (C) 2014 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
