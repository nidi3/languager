package stni.languager.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import stni.languager.online.LanguagerServer;

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
