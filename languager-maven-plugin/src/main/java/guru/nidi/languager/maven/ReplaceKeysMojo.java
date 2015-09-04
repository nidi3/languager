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

import guru.nidi.languager.crawl.*;
import guru.nidi.languager.online.LanguagerServer;
import guru.nidi.languager.online.OnlineReplaceRegexAction;
import guru.nidi.languager.online.OnlineReplacer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Replace all keys matching a regex by their respective translations. This is done for every available language.
 *
 * @author stni
 */
@Mojo(name = "replaceKeys", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class ReplaceKeysMojo extends AbstractOutputMojo {
    /**
     * The name of a class in the classpath. Its main method will be invoked before the replacement takes place.
     */
    @Parameter(property = "customizerClass")
    protected String customizerClass;

    /**
     * Add a special code to every replaced text to enable online translation. This works only in HTML files.
     */
    @Parameter(property = "onlineTranslation", defaultValue = "false")
    protected boolean onlineTranslation;

    /**
     * If 'onlineTranslation' is true, should the translation server be started?
     */
    @Parameter(property = "startServer", defaultValue = "true")
    protected boolean startServer = true;

    /**
     * The HTTP port to be used.
     */
    @Parameter(property = "port", defaultValue = "8880")
    private int port = 8880;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (customizerClass != null) {
            getLog().info("Running customizer " + customizerClass);
            try {
                extendClasspathWithCompile();
                final Class<?> customizer = Class.forName(customizerClass.replace('/', '.'), true, Thread.currentThread().getContextClassLoader());
                final Method main = customizer.getMethod("main", String[].class);
                main.invoke(null, (Object) new String[]{project.getBasedir().getAbsolutePath()});
            } catch (Exception e) {
                throw new MojoExecutionException("Problem running customizer", e);
            }
        }

        getLog().info("Start replacing keys");
        writePerLanguage(true);

        if (onlineTranslation && startServer) {
            try {
                getLog().info("Server started, end with ctrl-c");
                final LanguagerServer server = new LanguagerServer(8880, getCsvFile(), csvEncoding, csvSeparator);
                server.start();
            } catch (Exception e) {
                throw new MojoExecutionException("Problem running server", e);
            }
        }
    }

    protected CrawlAction doPerLanguage(ReplaceSearch search, Properties p, File targetDir) {
        if (search.getRegex() == null) {
            return new ReplacePropertiesAction(p, targetDir);
        }

        final Replacer replacer = onlineTranslation
                ? new OnlineReplacer(search.getReplacement(), search.getParameterMarker(), search.getParameterSeparator(), p, search.getEscapes())
                : new DefaultReplacer(search.getReplacement(), search.getParameterMarker(), search.getParameterSeparator(), p, search.getEscapes());

        ReplaceRegexActionParameter actionParameter = new ReplaceRegexActionParameter(targetDir, replacer);

        return onlineTranslation
                ? new OnlineReplaceRegexAction(search.getRegex(), null, actionParameter, port)
                : new ReplaceRegexAction(search.getRegex(), null, actionParameter);
    }
}