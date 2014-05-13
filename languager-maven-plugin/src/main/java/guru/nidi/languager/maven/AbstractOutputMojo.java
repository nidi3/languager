/*
 * Copyright (C) 2014 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.languager.maven;

import guru.nidi.languager.Util;
import guru.nidi.languager.crawl.CrawlAction;
import guru.nidi.languager.crawl.CrawlPattern;
import guru.nidi.languager.crawl.FileCrawler;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

/**
 *
 */
public abstract class AbstractOutputMojo extends AbstractI18nMojo {
    protected static final String PROPERTIES = ".properties";

    /**
     * The directory where the translated files are written to.
     */
    @Parameter(property = "replacedDirectory", defaultValue = "target/${project.build.finalName}")
    protected File replacedDirectory;

    /**
     * ReplaceSearch expressions which define the files to be processed.
     * A ReplaceSearch can contain 'regex', 'includes', 'excludes', 'encoding', 'replacement', 'parameterMarker', 'parameterSeparator', 'escapes'.
     */
    @Parameter(property = "searches")
    protected List<ReplaceSearch> searches;

    /**
     * The directory containing the properties files which define the languages to be processed.
     */
    @Parameter(property = "propertiesDirectory", defaultValue = "target/generated-sources")
    protected File propertiesDirectory;

    /**
     * The name of the properties files to be used.
     */
    @Parameter(property = "baseName")
    protected String baseName;

    protected void writePerLanguage(boolean loadProperties) throws MojoExecutionException {
        try {
            List<File> props = Util.getFiles(propertiesDirectory, baseName + "_*" + PROPERTIES, null);
            for (File prop : props) {
                int pos = prop.getName().indexOf("_");
                String lang = prop.getName().substring(pos + 1, prop.getName().length() - PROPERTIES.length());

                Properties p = null;
                if (loadProperties) {
                    p = new Properties();
                    p.load(new FileInputStream(prop));
                }

                for (ReplaceSearch search : searches) {
                    final File targetDir = new File(replacedDirectory, lang);
                    FileCrawler crawler = new FileCrawler(
                            new CrawlPattern(new File(searchBasedir().getAbsolutePath().replace("$lang", lang)), search.getIncludes(), search.getExcludes(), search.getEncoding()));
                    crawler.crawl(doPerLanguage(search, p, targetDir));
                }
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Problem writing output", e);
        }
    }

    protected abstract CrawlAction doPerLanguage(ReplaceSearch search, Properties p, File targetDir);
}