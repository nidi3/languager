package stni.languager.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import stni.languager.FindResult;
import stni.languager.KeyExtractor;
import stni.languager.crawl.CrawlPattern;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static stni.languager.crawl.FindRegexAction.Flag.TRIM;
import static stni.languager.crawl.FindRegexAction.Flag.WITH_EMPTY;

/**
 * Extract translation keys and their default translations out of source files and write them into a csv file.
 *
 * @author stni
 */
@Mojo(name = "extractKeys", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class ExtractKeysMojo extends AbstractI18nMojo {

    /**
     * ExtractSearch expressions which define the files to be processed.
     * A ExtractSearch can contain 'regex', 'includes', 'excludes', 'encoding', 'negativeRegex', 'ignoreRegex'.
     */
    @Parameter(property = "searches")
    protected List<ExtractSearch> searches = Collections.emptyList();

    /**
     * Locations of properties files inside the classpath to also process.
     */
    @Parameter(property = "propertyLocations")
    protected List<String> propertyLocations = Collections.emptyList();

    /**
     * Remove newline characters in translations.
     */
    @Parameter(property = "removeNewlines", defaultValue = "true")
    protected boolean removeNewLines = true;

    private KeyExtractor extractor = new KeyExtractor();

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Start extracting message keys");
        try {
            initLogger();

            extractFromFiles();

            if (!getLogger().isEmpty()) {
                checkSameDefaultValues();
                checkUnmessagedStrings();
                checkSameKeys();
            }

            extendClasspathWithCompile();
            extractor.extractFromClasspath(propertyLocations);

            if (removeNewLines) {
                extractor.removeNewlines();
            }

            extractor.writeCsv(getCsvFile(), csvEncoding, csvSeparator);
        } catch (Exception e) {
            throw new MojoExecutionException("Problem extracting keys", e);
        } finally {
            getLogger().close();
        }
    }

    private void extractFromFiles() throws IOException {
        for (ExtractSearch search : searches) {
            if (search.getRegex() != null) {
                extractor.extractFromFiles(
                        new CrawlPattern(searchBasedir(), search.getIncludes(), search.getExcludes(), search.getEncoding()),
                        search.getRegex(), EnumSet.of(TRIM, WITH_EMPTY));
            }
            if (search.getNegativeRegex() != null) {
                extractor.extractNegativesFromFiles(
                        new CrawlPattern(searchBasedir(), search.getIncludes(), search.getExcludes(), search.getEncoding()),
                        search.getNegativeRegex(), search.getIgnoreRegex(), EnumSet.of(TRIM));
            }
        }
    }

    private void checkSameKeys() {
        getLogger().logSection("Identical keys with different values:");
        for (KeyExtractor.FindResultPair same : extractor.getSameKeyResults()) {
            getLogger().log("'" + extractor.keyOf(same.getResult1()) + "'");
            getLogger().log(location(same.getResult1()));
            getLogger().log(location(same.getResult2()));
        }
    }

    private void checkSameDefaultValues() {
        getLogger().logSection("Identical values with different keys:");
        for (KeyExtractor.FindResultPair same : extractor.getSameValueResults()) {
            getLogger().log("'" + extractor.keyOf(same.getResult1()) + "' / '" + extractor.keyOf(same.getResult2()) + "'");
            getLogger().log(location(same.getResult1()));
            getLogger().log(location(same.getResult2()));
        }
    }

    private void checkUnmessagedStrings() {
        getLogger().logSection("Texts that are not messagized:");
        for (FindResult<List<String>> negative : extractor.getNegatives()) {
            getLogger().log(pad(extractor.keyOf(negative).replace('\n', ' ').replace('\r', ' ')));
            getLogger().log(location(negative));
        }
    }

}
