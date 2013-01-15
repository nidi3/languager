package org.languager.maven;

import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import stni.languager.CsvAnalyzer;

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

            final CsvAnalyzer analyzer = new CsvAnalyzer(getCsvFile(), csvEncoding, csvSeparator);
            final List<List<String>> diffs = analyzer.compareDefaultValueWithLanguage(language);
            if (!diffs.isEmpty()) {
                getLogger().logSection("Entries with difference between default value and " + language + ":");
                for (List<String> diff : diffs) {
                    getLogger().log(analyzer.getKey(diff));
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error analyzing CSV", e);
        }
    }
}
