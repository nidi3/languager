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
import guru.nidi.languager.MessageLine;
import guru.nidi.languager.OccurrenceReader;
import guru.nidi.languager.Util;
import guru.nidi.languager.check.CsvAnalyzer;
import guru.nidi.languager.check.LinkChecker;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.util.List;

/**
 * Execute some checks over the translations.
 *
 * @author stni
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class CheckMojo extends AbstractI18nMojo {
    /**
     * If strings that look like URLs ('http://...') should be checked if they are working links.
     */
    @Parameter(property = "checkLinks")
    private boolean checkLinks;

    /**
     * Check that all translations for the given language equal the default translation.
     */
    @Parameter(property = "checkDefaultsEqual")
    private String checkDefaultsEqual;

    private List<MessageLine> messages;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            initLogger();

            messages = Util.readCsvFile(getCsvFile(), csvEncoding, csvSeparator);

            if (checkLinks) {
                checkLinks();
            }
            if (checkDefaultsEqual != null) {
                checkDefaults();
            }

        } catch (IOException e) {
            throw new MojoExecutionException("Error executing checks", e);
        }
    }

    private void checkLinks(){
        final LinkChecker checker = new LinkChecker(getCsvFile(), messages, getLogger());
        final List<FindResult<String>> brokenLinks = checker.findBrokenLinks();
        if (!brokenLinks.isEmpty()) {
            getLogger().logSection("Broken links:");
            for (FindResult<String> brokenLink : brokenLinks) {
                getLogger().log(pad(brokenLink.getFinding()));
                getLogger().log(location(brokenLink));
            }
        }
    }

    private void checkDefaults() throws IOException {
        final OccurrenceReader occurrenceReader = new OccurrenceReader(getCsvFile());
        final CsvAnalyzer analyzer = new CsvAnalyzer(getCsvFile(), messages);
        final List<FindResult<MessageLine>> diffs = analyzer.compareDefaultValueWithLanguage(checkDefaultsEqual);
        if (!diffs.isEmpty()) {
            getLogger().logSection("Entries with difference between default value and " + checkDefaultsEqual + ":");
            for (FindResult<MessageLine> diff : diffs) {
                final String key = diff.getFinding().readKey();
                getLogger().log(pad(key));
                getLogger().log(location(diff));
                for (String occurrence : occurrenceReader.getOccurrences(key)) {
                    getLogger().log(pad() + occurrence);
                }
            }
        }
    }
}
