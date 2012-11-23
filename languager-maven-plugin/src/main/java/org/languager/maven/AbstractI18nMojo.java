package org.languager.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * @phase generate-sources
 */
public abstract class AbstractI18nMojo extends AbstractMojo {
    /**
     * @parameter expression="${basedir}"
     * @required
     * @readonly
     */
    protected File basedir;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @parameter expression="${csvFile}" default-value="src/main/resources/messages.csv"
     */
    private File csvFile;

    /**
     * @parameter expression="${csvEncoding}" default-value="utf-8"
     */
    protected String csvEncoding;

    /**
     * @parameter expression="${csvSeparator}" default-value=';'
     */
    protected char csvSeparator = ';';

    protected File getCsvFile() {
        if (csvFile == null) {
            return new File(basedir, "src/main/resources/messages.csv");
        }
        return csvFile;
    }
}
