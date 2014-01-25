package stni.languager.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import stni.languager.FindResult;
import stni.languager.MessageLine;
import stni.languager.OccurrenceReader;
import stni.languager.Util;
import stni.languager.check.CsvAnalyzer;
import stni.languager.check.LinkChecker;

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

    private void checkLinks() throws IOException {
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
