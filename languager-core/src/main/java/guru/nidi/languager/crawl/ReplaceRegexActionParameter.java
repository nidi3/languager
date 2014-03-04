package guru.nidi.languager.crawl;

import java.io.File;

/**
 *
 */
public class ReplaceRegexActionParameter {

    private final File targetDir;
    private final Replacer replacer;

    public ReplaceRegexActionParameter(File targetDir, Replacer replacer) {
        this.targetDir = targetDir;
        this.replacer = replacer;
    }

    public Replacer getReplacer() {
        return replacer;
    }

    public File getTargetDir() {
        return targetDir;
    }

}
