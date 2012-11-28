package org.languager.maven;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;

import stni.languager.KeyExtractor;
import stni.languager.crawl.CrawlPattern;
import stni.languager.crawl.FindRegexAction;
import stni.languager.crawl.FindResult;

/**
 * @author stni
 * @goal extractKeys
 * @requiresDependencyResolution compile
 */
public class ExtractKeysMojo extends AbstractI18nMojo {

    /**
     * @parameter expression="${searches}"
     */
    protected List<ExtractSearch> searches = Collections.emptyList();

    /**
     * @parameter expression="${propertyLocations}"
     */
    protected List<String> propertyLocations = Collections.emptyList();

    /**
     * @parameter expression="${removeNewlines}" default-value="true"
     */
    protected boolean removeNewLines = true;

    /**
     * @parameter expression="${verbose}" default-value="true"
     */
    protected boolean verbose = true;


    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Start extracting message keys");
        final KeyExtractor extractor = new KeyExtractor();
        try {
            extractFromFiles(extractor);

            if (verbose) {
                checkSameDefaultValues(extractor);
                checkUnmessagedStrings(extractor);
            }
            checkSameKeys(extractor);

            extendPluginClasspath(project.getCompileClasspathElements());
            extractor.extractFromClasspath(propertyLocations);

            if (removeNewLines) {
                extractor.removeNewlines();
            }

            extractor.writeCsv(getCsvFile(), csvEncoding, csvSeparator);
        } catch (Exception e) {
            throw new MojoExecutionException("Problem extracting keys", e);
        }
    }

    private void extractFromFiles(KeyExtractor extractor) throws IOException {
        for (ExtractSearch search : searches) {
            if (search.getRegex() != null) {
                extractor.extractFromFiles(
                        new CrawlPattern(searchBasedir(), search.getIncludes(), search.getExcludes(), search.getEncoding()),
                        search.getRegex(), EnumSet.of(FindRegexAction.Flag.TRIM));
            }
            if (search.getNegativeRegex() != null) {
                extractor.extractNegativesFromFiles(
                        new CrawlPattern(searchBasedir(), search.getIncludes(), search.getExcludes(), search.getEncoding()),
                        search.getNegativeRegex(), EnumSet.of(FindRegexAction.Flag.TRIM));
            }
        }
    }

    private void checkSameKeys(KeyExtractor extractor) {
        String log = "";
        for (KeyExtractor.FindResultPair same : extractor.getSameKeyResults()) {
            log += "\n'" + extractor.keyOf(same.getResult1()) + "':\n" +
                    extractor.location(same.getResult1()) + "\n" +
                    extractor.location(same.getResult2());
        }
        if (log.length() > 0) {
            getLog().warn("******************Found identical keys with different values:" + log);
        }
    }

    private void checkSameDefaultValues(KeyExtractor extractor) {
        String log = "";
        for (KeyExtractor.FindResultPair same : extractor.getSameValueResults()) {
            log += "\n'" + extractor.keyOf(same.getResult1()) + "' / '" + extractor.keyOf(same.getResult2()) + "':\n" +
                    extractor.location(same.getResult1()) + "\n" +
                    extractor.location(same.getResult2());
        }
        if (log.length() > 0) {
            getLog().warn("******************Found identical values with different keys:" + log);
        }
    }

    private void checkUnmessagedStrings(KeyExtractor extractor) {
        String log = "";
        for (FindResult negative : extractor.getNegatives()) {
            log += "\n" + pad(extractor.keyOf(negative)) + " at " + extractor.location(negative) + "\n";
        }
        if (log.length() > 0) {
            getLog().warn("******************Found texts that are not messagized:" + log);
        }
    }

    private String pad(String s) {
        if (s.length() >= 30) {
            s = s.substring(0, 26) + "...'";
        }
        if (s.length() < 30) {
            s += "'";
        }
        while (s.length() < 30) {
            s += " ";
        }
        return "'" + s;
    }

    private void extendPluginClasspath(List<String> elements) throws MojoExecutionException {
        ClassWorld world = new ClassWorld();
        try {
            ClassRealm realm = world.newRealm("maven", Thread.currentThread().getContextClassLoader());
            for (String element : elements) {
                File elementFile = new File(element);
                getLog().debug("*** Adding element to plugin classpath " + elementFile.getPath());
                realm.addConstituent(elementFile.toURI().toURL());
            }
            Thread.currentThread().setContextClassLoader(realm.getClassLoader());
        } catch (Exception ex) {
            throw new MojoExecutionException(ex.toString(), ex);
        }
    }
}
