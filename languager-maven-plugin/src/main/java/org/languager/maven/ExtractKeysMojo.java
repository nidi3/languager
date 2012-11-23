package org.languager.maven;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;

import stni.languager.FileCrawler;
import stni.languager.FindRegexAction;
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
            if (searchPaths != null) {
                FileCrawler<RegexCrawlPattern, FindRegexAction> crawler = FileCrawler.create(basedir, new FindRegexAction());
                for (RegexCrawlPattern searchPath : searchPaths) {
                    if (searchPath.getEncoding() == null) {
                        searchPath.setEncoding("utf-8");
                    }
                    crawler.addCrawlPattern(searchPath);
                }
                for (List<String> keys : crawler.crawl().getResults()) {
                    messages.put(keys.get(0), new Message(keys.get(0), true, keys.size() > 1 ? keys.get(1) : null));
                }
            }

            if (propertyLocations != null) {
                extendPluginClasspath(project.getCompileClasspathElements());

                PropertiesFinder finder = new PropertiesFinder();
                for (String propertyLocation : propertyLocations) {
                    finder.addPropertyLocation(propertyLocation);
                }
                messages.putAll(finder.findProperties());
            }

            if (removeNewLines) {
                for (Map.Entry<String, Message> message : messages.entrySet()) {
                    messages.put(message.getKey(), message.getValue().transformed(newLineRemover));
                }
            }

            getCsvFile().getParentFile().mkdirs();

            MessagesWriter writer = new MessagesWriter(csvEncoding, csvSeparator);
            writer.write(getCsvFile(), messages);

        } catch (Exception e) {
            throw new MojoExecutionException("Problem extracting keys", e);
        }
    }

    private void extendPluginClasspath(List<String> elements) throws MojoExecutionException {
        ClassWorld world = new ClassWorld();
        try {
            ClassRealm realm = world.newRealm("edoras-formlets-tools-maven", Thread.currentThread().getContextClassLoader());
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
