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
import guru.nidi.languager.PropertiesUtils;
import guru.nidi.languager.SourcePosition;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

/**
 * Check that properties files have a correct format.
 *
 * @author stni
 */
@Mojo(name = "checkProperties", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class CheckPropertiesMojo extends AbstractLoggingMojo {

    /**
     * If all values are interpreted as textFormats (and thus must escape ')
     */
    @Parameter(property = "alwaysUseTextFormat")
    private boolean alwaysUseTextFormat;

    /**
     *
     */
    @Parameter(property = "includes", defaultValue = "src/main/resources/**/*")
    private String includes = "src/main/resources/**/*";

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            initLogger();
            getLogger().logSection("Properties files with suspicious apostrophes:");
            List<File> files = FileUtils.getFiles(project.getBasedir(), getIncludes(), null);
            for (File file : files) {
                checkFile(file);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Problem reading files", e);
        }
    }

    private String getIncludes() {
        List<String> includeList = new ArrayList<>();
        for (String include : includes.split(",")) {
            includeList.add(include.endsWith(".properties") ? include : (include + ".properties"));
        }
        return StringUtils.collectionToCommaDelimitedString(includeList);
    }

    private void checkFile(File file) throws IOException {
        PropFile propFile = new PropFile(file);
        for (Map.Entry<String, String> entry : propFile.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            int singleQuotePos = PropertiesUtils.findFirstSingleQuote(value, alwaysUseTextFormat);
            if (singleQuotePos >= 0) {
                int line = propFile.findKey(key);
                getLogger().log(pad(value));
                getLogger().log(location(new FindResult<>(new SourcePosition(file, line, key.length() + 2 + singleQuotePos), null)));
            }
        }
    }

    private static class PropFile {
        private final File file;
        private final Properties props = new Properties();
        private List<String> lines;

        private PropFile(File file) throws IOException {
            this.file = file;
            try (InputStream in = new FileInputStream(file)) {
                props.load(in);
            }
        }

        private List<String> readFile() throws IOException {
            List<String> lines = new ArrayList<>();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "iso-8859-1"))) {
                while (in.ready()) {
                    lines.add(in.readLine());
                }
            }
            return lines;
        }

        public int findKey(String key) throws IOException {
            if (lines == null) {
                lines = readFile();
            }
            int i = 0;
            for (String line : lines) {
                i++;
                if (line.startsWith(key)) {
                    return i;
                }
            }
            return 0;
        }

        public Set<Map.Entry<String, String>> entrySet() {
            @SuppressWarnings("unchecked")
            Set<Map.Entry<String, String>> set = (Set) props.entrySet();
            return set;
        }

    }
}
