package stni.languager.crawl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.EnumSet;
import java.util.regex.Matcher;

/**
 *
 */
public class ReplaceRegexAction extends FindRegexAction {
    private final ReplaceRegexActionParameter parameter;

    public ReplaceRegexAction(String regex, EnumSet<Flag> flags, ReplaceRegexActionParameter parameter) {
        super(regex, flags);
        this.parameter = parameter;
    }

    @Override
    protected void doAction(File basedir, File file, String content, CrawlPattern pattern) throws IOException {
        Matcher matcher = getRegex().matcher(content);
        StringBuffer s = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(s, parameter.getReplacer().replace(matcher));
        }
        matcher.appendTail(s);
        String relativePath = file.getParentFile().getAbsolutePath().substring(basedir.getAbsolutePath().length());
        File target = new File(parameter.getTargetDir(), relativePath);
        target.mkdirs();
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(new File(target, file.getName())), pattern.getEncoding());
            out.write(s.toString());
        } finally {
            Util.closeSilently(out);
        }
    }

}
