package org.languager.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

/**
 * @phase generate-sources
 */
public abstract class AbstractI18nMojo extends AbstractMojo {
    /**
     * @parameter expression="${searchBasedir}"
     * @required
     * @readonly
     */
    private File searchBasedir;

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

    protected File searchBasedir() {
        return searchBasedir != null ? searchBasedir : project.getBasedir();
    }

    protected File getCsvFile() {
        if (csvFile == null) {
            return new File(project.getBasedir(), "src/main/resources/messages.csv");
        }
        return csvFile;
    }
}
