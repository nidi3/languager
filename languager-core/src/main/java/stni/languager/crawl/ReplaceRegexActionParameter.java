package stni.languager.crawl;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 *
 */
public class ReplaceRegexActionParameter {
    interface Replacer {
        String replace(Matcher m);
    }

    private final File targetDir;
    private final Replacer replacer;

    public ReplaceRegexActionParameter(File targetDir, final String replacement, final Properties properties, final List<Escape> escapes) {
        this.targetDir = targetDir;
        this.replacer = new Replacer() {
            public String replace(Matcher m) {
                return replacement.replace("$1", value(m, 1)).replace("$2", value(m, 2));
            }

            private String value(Matcher m, int group) {
                String s = properties.getProperty(m.group(group));
                if (s == null) {
                    return "";
                }
                if (escapes != null) {
                    for (Escape escape : escapes) {
                        s = s.replace(escape.getFrom(), escape.getTo());
                    }
                }
                return s;
            }
        };
    }

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
