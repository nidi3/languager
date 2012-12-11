package stni.languager.crawl;

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

    private final File targetDir;
    private final Replacer replacer;

    public ReplaceRegexActionParameter(File targetDir, final String replacement, final String parameterMarker, final String parameterSeparator, final Properties properties, final List<Escape> escapes) {
        this.targetDir = targetDir;
        this.replacer = new Replacer() {
            public String replace(Matcher m) {
                String replaced = replacement
                        .replace("$1", value(m, 1))
                        .replace("$2", value(m, 2));
                return applyParameters(m, replaced);
            }

            private String applyParameters(Matcher m, String s) {
                if (parameterMarker == null || parameterMarker.length() == 0) {
                    return s;
                }
                List<String> parameters = extractParameters(m);

                String res = s;
                for (String parameter : parameters) {
                    int pos = res.indexOf(parameterMarker);
                    if (pos < 0) {
                        break;
                    }
                    res = res.substring(0, pos) + parameter + res.substring(pos + 2);
                }

                return res;
            }

            private List<String> extractParameters(Matcher m) {
                List<String> parameters = new ArrayList<String>();
                for (int i = 2; i < m.groupCount(); i++) {
                    String group = m.group(i);
                    if (group == null) {
                        parameters.add("");
                    } else {
                        if (parameterSeparator == null || parameterSeparator.length() == 0) {
                            parameters.add(group);
                        } else {
                            for (String part : group.split(parameterSeparator)) {
                                parameters.add(part);
                            }
                        }
                    }
                }
                return parameters;
            }

            private String value(Matcher m, int group) {
                String g = m.group(group);
                if (g == null) {
                    return "";
                }
                String s = properties.getProperty(g);
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
