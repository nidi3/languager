package org.languager.maven;

import static stni.languager.crawl.FindRegexAction.Flag.TRIM;
import static stni.languager.crawl.FindRegexAction.Flag.WITH_EMPTY;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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

    private KeyExtractor extractor = new KeyExtractor();
    private PrintWriter logfile;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Start extracting message keys");
        try {
            logfile = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(project.getBasedir(), "target/languager.log")), "utf-8"));
            extractFromFiles();

            if (verbose) {
                checkSameDefaultValues();
                checkUnmessagedStrings();
                checkSameKeys();
            }

            extendPluginClasspath(project.getCompileClasspathElements());
            extractor.extractFromClasspath(propertyLocations);

            if (removeNewLines) {
                extractor.removeNewlines();
            }

            extractor.writeCsv(getCsvFile(), csvEncoding, csvSeparator);
        } catch (Exception e) {
            throw new MojoExecutionException("Problem extracting keys", e);
        } finally {
            Util.closeSilently(logfile);
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
                        search.getNegativeRegex(), EnumSet.of(TRIM));
            }
        }
    }

    private void checkSameKeys() {
        logfile.println("******************Identical keys with different values:");
        for (KeyExtractor.FindResultPair same : extractor.getSameKeyResults()) {
            logfile.println(extractor.keyOf(same.getResult1()) + "':");
            logfile.println(extractor.location(same.getResult1()));
            logfile.println(extractor.location(same.getResult2()));
        }
    }

    private void checkSameDefaultValues() {
        logfile.println("******************Identical values with different keys:");
        for (KeyExtractor.FindResultPair same : extractor.getSameValueResults()) {
            logfile.println(extractor.keyOf(same.getResult1()) + "' / '" + extractor.keyOf(same.getResult2()) + "':");
            logfile.println(extractor.location(same.getResult1()));
            logfile.println(extractor.location(same.getResult2()));
        }
    }

    private void checkUnmessagedStrings() {
        logfile.println("******************Texts that are not messagized:");
        for (FindResult negative : extractor.getNegatives()) {
            logfile.println(pad(extractor.keyOf(negative).replace('\n', ' ').replace('\r', ' ')) + " at " + extractor.location(negative));
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
