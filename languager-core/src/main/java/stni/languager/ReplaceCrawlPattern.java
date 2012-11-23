package stni.languager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 * Created with IntelliJ IDEA.
 * User: nidi
 * Date: 16.09.12
 * Time: 02:31
 * To change this template use File | Settings | File Templates.
 */
public class ReplaceCrawlPattern extends RegexCrawlPattern {
    interface Replacer {
        String replace(Matcher m);
    }

    private Replacer replacer;
    private File targetDir;
    private String replacement;
    private List<Escape> escapes = new ArrayList<Escape>();

    public ReplaceCrawlPattern() {
    }

    public ReplaceCrawlPattern(String regex, String includes, String excludes, String encoding, File targetDir, final String replacement, final Properties properties) {
        this(regex, includes, excludes, encoding, targetDir, null);
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

    public ReplaceCrawlPattern(String regex, String includes, String excludes, String encoding, File targetDir, Replacer replacer) {
        super(regex, includes, excludes, encoding);
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
