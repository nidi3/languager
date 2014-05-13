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

import guru.nidi.languager.Logger;
import org.apache.maven.plugin.logging.Log;

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
