package guru.nidi.languager.online;

import guru.nidi.languager.crawl.DefaultReplacer;
import guru.nidi.languager.crawl.Escape;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 *
 */
public class OnlineReplacer extends DefaultReplacer {
    public OnlineReplacer(String replacement, String parameterMarker, String parameterSeparator, Properties properties, List<Escape> escapes) {
        super(replacement, parameterMarker, parameterSeparator, properties, escapes);
    }

    @Override
    public String replace(File f, Matcher m) {
        String res = super.replace(f, m);
        if (Util.isOnline(f)) {
            res += " <span class='__langMarker' onmouseover=\"__lang.show(event,'" + escape(m.group(1)) + "')\" onmouseout=__lang.hide()/>";
        }
        return res;
    }

    private String escape(String s) {
        return s.replace("'", "\\\\'").replace("\"", "\\\\'");
    }
}
