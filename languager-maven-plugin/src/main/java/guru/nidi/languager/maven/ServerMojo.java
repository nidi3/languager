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

import guru.nidi.languager.online.LanguagerServer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 */
@Mojo(name = "server", defaultPhase = LifecyclePhase.INITIALIZE)
public class ServerMojo extends AbstractI18nMojo {
    /**
     * The HTTP port to be used.
     */
    @Parameter(property = "port", defaultValue = "8880")
    private int port = 8880;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            initLogger();
            getLogger().log("Server started, end with ctrl-c");
            final LanguagerServer server = new LanguagerServer(port, getCsvFile(), csvEncoding, csvSeparator);
            server.start();
        } catch (Exception e) {
            throw new MojoExecutionException("Problem running server", e);
        }
    }
}
