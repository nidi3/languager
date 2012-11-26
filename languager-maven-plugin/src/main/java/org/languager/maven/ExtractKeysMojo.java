package org.languager.maven;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;

import stni.languager.FileCrawler;
import stni.languager.FindRegexAction;
import stni.languager.FindResult;
import stni.languager.Message;
import stni.languager.MessagesWriter;
import stni.languager.PropertiesFinder;
import stni.languager.RegexCrawlPattern;

/**
 * @author stni
 * @goal extractKeys
 * @requiresDependencyResolution compile
 */
public class ExtractKeysMojo extends AbstractI18nMojo {

    /**
     * @parameter expression="${searchPaths}"
     */
    protected List<RegexCrawlPattern> searchPaths;

    /**
     * @parameter expression="${propertyLocations}"
     */
    protected List<String> propertyLocations;

    /**
     * @parameter expression="${removeNewlines}" default-value="true"
     */
    protected boolean removeNewLines = true;

    private static final Message.Transformer newLineRemover = new Message.Transformer() {
        public String transform(String lang, String value) {
            return value.replaceAll("\\r?\\n\\s*", " ").trim();
        }
    };

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Start extracting message keys");
        SortedMap<String, Message> messages = new TreeMap<String, Message>();

        try {
            extractFromFiles(messages);
            extractFromClasspath(messages);
            removeNewlines(messages);
            writeCsv(messages);
        } catch (Exception e) {
            throw new MojoExecutionException("Problem extracting keys", e);
        }
    }

    private void extractFromFiles(SortedMap<String, Message> messages) throws IOException {
        if (searchPaths != null) {
            Map<String, FindResult> resultsByKey = new HashMap<String, FindResult>();
            Map<String, FindResult> resultsByDefaultValue = new HashMap<String, FindResult>();

            FileCrawler<RegexCrawlPattern, FindRegexAction> crawler = initCrawler();

            for (FindResult result : crawler.crawl().getResults()) {
                checkSameKey(resultsByKey, result);
                checkSameDefaultValue(resultsByDefaultValue, result);
                messages.put(keyOf(result), new Message(keyOf(result), true, defaultValueOf(result)));
            }
        }
    }

    private FileCrawler<RegexCrawlPattern, FindRegexAction> initCrawler() {
        FileCrawler<RegexCrawlPattern, FindRegexAction> crawler = FileCrawler.create(basedir, new FindRegexAction());
        for (RegexCrawlPattern searchPath : searchPaths) {
            if (searchPath.getEncoding() == null) {
                searchPath.setEncoding("utf-8");
            }
            crawler.addCrawlPattern(searchPath);
        }
        return crawler;
    }

    private void checkSameKey(Map<String, FindResult> resultsByKey, FindResult result) {
        String defaultValue = defaultValueOf(result);
        String key = keyOf(result);
        final FindResult sameKey = resultsByKey.get(key);
        if (sameKey != null && !nullSafeEquals(defaultValue, defaultValueOf(sameKey))) {
            getLog().warn("******************Found identical key '" + key + "' with different default values:\n" +
                    location(result) + "\n" + location(sameKey));
        }
        resultsByKey.put(key, result);
    }

    private void checkSameDefaultValue(Map<String, FindResult> resultsByDefaultValue, FindResult result) {
        String defaultValue = defaultValueOf(result);
        String key = keyOf(result);
        final FindResult sameDefaultValue = resultsByDefaultValue.get(defaultValue);
        if (sameDefaultValue != null && !key.equals(keyOf(sameDefaultValue))) {
            getLog().warn("******************Found identical default value with different keys:\n" +
                    "'" + key + "': " + location(result) + "\n" +
                    "'" + keyOf(sameDefaultValue) + "': " + location(sameDefaultValue));
        }
        resultsByDefaultValue.put(defaultValue, result);
    }

    private String location(FindResult result) {
        return result.getSource() + ":" + result.getLine() + ":" + result.getColumn();
    }

    private String defaultValueOf(FindResult result) {
        return result.getFindings().size() > 1 ? result.getFindings().get(1) : null;
    }

    private String keyOf(FindResult result) {
        return result.getFindings().get(0);
    }

    private boolean nullSafeEquals(String a, String b) {
        return a == b || (a != null && a.equals(b));
    }

    private void extractFromClasspath(SortedMap<String, Message> messages) throws MojoExecutionException, DependencyResolutionRequiredException, IOException {
        if (propertyLocations != null) {
            extendPluginClasspath(project.getCompileClasspathElements());

            PropertiesFinder finder = new PropertiesFinder();
            for (String propertyLocation : propertyLocations) {
                finder.addPropertyLocation(propertyLocation);
            }
            messages.putAll(finder.findProperties());
        }
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

    private void removeNewlines(SortedMap<String, Message> messages) {
        if (removeNewLines) {
            for (Map.Entry<String, Message> message : messages.entrySet()) {
                messages.put(message.getKey(), message.getValue().transformed(newLineRemover));
            }
        }
    }

    private void writeCsv(SortedMap<String, Message> messages) throws IOException {
        getCsvFile().getParentFile().mkdirs();

        MessagesWriter writer = new MessagesWriter(csvEncoding, csvSeparator);
        writer.write(getCsvFile(), messages);
    }
}
