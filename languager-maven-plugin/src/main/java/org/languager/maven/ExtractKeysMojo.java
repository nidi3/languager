package org.languager.maven;

import static stni.languager.crawl.FindRegexAction.Flag.TRIM;
import static stni.languager.crawl.FindRegexAction.Flag.WITH_EMPTY;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
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
     * @parameter expression="${log}" default-value="console"
     */
    protected String log = "console";

    private KeyExtractor extractor = new KeyExtractor();
    private DelegatingLogger logger;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Start extracting message keys");
        try {
            initLogger();

            extractFromFiles();

            if (!logger.isEmpty()) {
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
            logger.close();
        }
    }

    private void initLogger() throws IOException {
        PrintWriter printWriter = null;
        Log mavenLog = null;
        if (log != null && log.length() > 0) {
            for (String logName : log.split(",")) {
                if ("console".equalsIgnoreCase(logName)) {
                    mavenLog = getLog();
                } else if ("file".equalsIgnoreCase(logName)) {
                    File target = new File(project.getBuild().getDirectory());
                    target.mkdirs();
                    printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(target, "languager.log")), "utf-8"));
                }
            }
        }
        logger = new DelegatingLogger(printWriter, mavenLog);
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
        logger.log("******************Identical keys with different values:");
        for (KeyExtractor.FindResultPair same : extractor.getSameKeyResults()) {
            logger.log(extractor.keyOf(same.getResult1()) + "':");
            logger.log(location(same.getResult1()));
            logger.log(location(same.getResult2()));
        }
    }

    private String location(FindResult findResult) {
        return findResult.getSource().getAbsolutePath() + " (" + findResult.getSource().getName() + ":" + findResult.getLine() + ")";
    }

    private void checkSameDefaultValues() {
        logger.log("******************Identical values with different keys:");
        for (KeyExtractor.FindResultPair same : extractor.getSameValueResults()) {
            logger.log(extractor.keyOf(same.getResult1()) + "' / '" + extractor.keyOf(same.getResult2()) + "':");
            logger.log(location(same.getResult1()));
            logger.log(location(same.getResult2()));
        }
    }

    private void checkUnmessagedStrings() {
        logger.log("******************Texts that are not messagized:");
        for (FindResult negative : extractor.getNegatives()) {
            logger.log(pad(extractor.keyOf(negative).replace('\n', ' ').replace('\r', ' ')) + " at " + location(negative));
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
