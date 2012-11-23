package stni.languager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: stni
 * Date: 09.03.12
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
public class ReplaceRegexAction extends AbstractContentReadingCrawlAction<ReplaceCrawlPattern> {
    @Override
    protected void doAction(File basedir, File file, String content, ReplaceCrawlPattern pattern) throws IOException {
        Matcher matcher = pattern.getRegexPattern().matcher(content);
        StringBuffer s = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(s, pattern.getReplacer().replace(matcher));
        }
        matcher.appendTail(s);
        String relativePath = file.getParentFile().getAbsolutePath().substring(basedir.getAbsolutePath().length());
        File target = new File(pattern.getTargetDir(), relativePath);
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
