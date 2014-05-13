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

import guru.nidi.languager.crawl.CopyAction;
import guru.nidi.languager.crawl.CrawlAction;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.util.Properties;

/**
 * Copy files into all translated directories.
 *
 * @author stni
 */
@Mojo(name = "copyFiles", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class CopyFilesMojo extends AbstractOutputMojo {
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Start copying files");
        writePerLanguage(false);
    }

    @Override
    protected CrawlAction doPerLanguage(ReplaceSearch search, Properties p, File targetDir) {
        return new CopyAction(targetDir);
    }
}