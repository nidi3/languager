package org.languager.maven;

import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import stni.languager.CsvAnalyzer;
import stni.languager.FindResult;
import stni.languager.OccurrenceReader;

/**
 * @author stni
 * @goal checkDefaultLanguage
 */
public class DefaultLanguageCheckMojo extends AbstractI18nMojo {
    /**
     * @parameter expression="${language}"
     * @required
     */
    protected String language;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            initLogger();

            final OccurrenceReader occurrenceReader = new OccurrenceReader(getCsvFile());
            final CsvAnalyzer analyzer = new CsvAnalyzer(getCsvFile(), csvEncoding, csvSeparator);
            final List<FindResult> diffs = analyzer.compareDefaultValueWithLanguage(language);
            if (!diffs.isEmpty()) {
                getLogger().logSection("Entries with difference between default value and " + language + ":");
                for (FindResult diff : diffs) {
                    getLogger().log(pad(analyzer.getKey(diff)));
                    getLogger().log(location(diff));
                    for (String occurrence : occurrenceReader.getOccurrences(analyzer.getKey(diff))) {
                        getLogger().log(pad() + occurrence);
                    }
                }
            }

        } catch (IOException e) {
            throw new MojoExecutionException("Error analyzing CSV", e);
        }
    }
}
