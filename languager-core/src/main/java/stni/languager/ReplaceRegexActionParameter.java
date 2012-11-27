package stni.languager;

import java.io.File;
import java.util.ArrayList;
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

    private Replacer replacer;
    private File targetDir;
    private String replacement;
    private List<Escape> escapes = new ArrayList<Escape>();

    public ReplaceRegexActionParameter(File targetDir, final String replacement, final Properties properties) {
        this.targetDir = targetDir;
        setReplacer(new Replacer() {
            public String replace(Matcher m) {
                return replacement.replace("$1", value(m, 1)).replace("$2", value(m, 2));
            }

            private String value(Matcher m, int group) {
                String s = properties.getProperty(m.group(group));
                if (s == null) {
                    return "";
                }
                for (Escape escape : escapes) {
                    s = s.replace(escape.getFrom(), escape.getTo());
                }
                return s;
            }
        });
    }

    public ReplaceRegexActionParameter(File targetDir, Replacer replacer) {
        this.targetDir = targetDir;
        this.replacer = replacer;
    }

    public Replacer getReplacer() {
        return replacer;
    }

    public void setReplacer(Replacer replacer) {
        this.replacer = replacer;
    }

    public File getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(File targetDir) {
        this.targetDir = targetDir;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public List<Escape> getEscapes() {
        return escapes;
    }

    public void setEscapes(List<Escape> escapes) {
        this.escapes = escapes;
    }
}
