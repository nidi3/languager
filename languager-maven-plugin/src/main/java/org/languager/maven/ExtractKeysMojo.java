package org.languager.maven;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;

import stni.languager.CrawlPattern;
import stni.languager.KeyExtractor;

/**
 * @author stni
 * @goal extractKeys
 * @requiresDependencyResolution compile
 */
public class ExtractKeysMojo extends AbstractI18nMojo {

    /**
     * @parameter expression="${searchPaths}"
     */
    protected List<CrawlPattern> searchPaths = Collections.emptyList();

    /**
     * @parameter expression="${propertyLocations}"
     */
    protected List<String> propertyLocations = Collections.emptyList();

    /**
     * @parameter expression="${removeNewlines}" default-value="true"
     */
    protected boolean removeNewLines = true;



    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Start extracting message keys");
        final KeyExtractor extractor = new KeyExtractor();
        try {
            extractor.extractFromFiles(basedir, searchPaths);
            checkSameDefaultValues(extractor);
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

    private void checkSameKeys(KeyExtractor extractor) {
        for (KeyExtractor.FindResultPair same : extractor.getSameKeyResults()) {
            getLog().warn("******************Found identical key '" + extractor.keyOf(same.getResult1()) + "' with different default values:\n" +
                    extractor.location(same.getResult1()) + "\n" + extractor.location(same.getResult2()));
        }
    }

    private void checkSameDefaultValues(KeyExtractor extractor) {
        for (KeyExtractor.FindResultPair same : extractor.getSameDefaultValueResults()) {
            getLog().warn("******************Found identical default value with different keys:\n" +
                    "'" + extractor.keyOf(same.getResult1()) + "': " + extractor.location(same.getResult1()) + "\n" +
                    "'" + extractor.keyOf(same.getResult2()) + "': " + extractor.location(same.getResult2()));
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
}
