package org.languager.maven;

import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import stni.languager.CsvAnalyzer;
import stni.languager.FindResult;
import stni.languager.LinkChecker;
import stni.languager.OccurrenceReader;
import stni.languager.Util;

/**
 * @author stni
 * @goal check
 */
public class CheckMojo extends AbstractI18nMojo {
    /**
     * @parameter expression="${checkLinks}"
     */
    private boolean checkLinks;

    /**
     * @parameter expression="${checkDefaultsEqual}"
     */
    private String checkDefaultsEqual;

    private List<List<String>> messages;

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

    private void checkLinks() throws IOException {
        final LinkChecker checker = new LinkChecker(getCsvFile(), messages);
        final List<FindResult> brokenLinks = checker.findBrokenLinks();
        if (!brokenLinks.isEmpty()) {
            getLogger().logSection("Broken links:");
            for (FindResult brokenLink : brokenLinks) {
                getLogger().log(pad(brokenLink.getFindings().get(0)));
                getLogger().log(location(brokenLink));
            }
        }
    }

    private void checkDefaults() throws IOException {
        final OccurrenceReader occurrenceReader = new OccurrenceReader(getCsvFile());
        final CsvAnalyzer analyzer = new CsvAnalyzer(getCsvFile(), messages);
        final List<FindResult> diffs = analyzer.compareDefaultValueWithLanguage(checkDefaultsEqual);
        if (!diffs.isEmpty()) {
            getLogger().logSection("Entries with difference between default value and " + checkDefaultsEqual + ":");
            for (FindResult diff : diffs) {
                getLogger().log(pad(analyzer.getKey(diff)));
                getLogger().log(location(diff));
                for (String occurrence : occurrenceReader.getOccurrences(analyzer.getKey(diff))) {
                    getLogger().log(pad() + occurrence);
                }
            }
        }
    }
}
