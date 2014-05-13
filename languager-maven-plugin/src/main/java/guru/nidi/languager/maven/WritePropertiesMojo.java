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

import guru.nidi.languager.PropertiesWriter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * Write a set of properties files out of a csv file containing keys and translations.
 *
 * @author stni
 */
@Mojo(name = "writeProperties", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class WritePropertiesMojo extends AbstractI18nMojo {
    /**
     * The directory to write the properties files to.
     */
    @Parameter(property = "propertiesDirectory", defaultValue = "target/generated-sources")
    protected File propertiesDirectory;

    /**
     * The basename of the properties to be written.
     */
    @Parameter(property = "baseName", required = true)
    protected String baseName;


    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Start writing properties files");
        try {
            PropertiesWriter writer = new PropertiesWriter(csvSeparator);
            if (propertiesDirectory == null) {
                propertiesDirectory = new File(project.getBasedir(), "target/generated-sources");
            }
            propertiesDirectory.mkdirs();
            writer.write(getCsvFile(), csvEncoding, propertiesDirectory, baseName);
        } catch (IOException e) {
            throw new MojoExecutionException("Problem writing properties files", e);
        }
    }
}
