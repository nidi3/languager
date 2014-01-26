package stni.languager.online;

import stni.languager.crawl.CrawlPattern;
import stni.languager.crawl.ReplaceRegexAction;
import stni.languager.crawl.ReplaceRegexActionParameter;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

/**
 *
 */
public class OnlineReplaceRegexAction extends ReplaceRegexAction {
    private final int port;

    public OnlineReplaceRegexAction(String regex, EnumSet<Flag> flags, ReplaceRegexActionParameter actionParameter, int port) {
        super(regex, flags, actionParameter);
        this.port = port;
    }

    @Override
    protected void doAction(File basedir, File file, String content, CrawlPattern pattern) throws IOException {
        if (Util.isOnline(file)) {
            int bodyStart = content.indexOf("<body");
            if (bodyStart > 0) {
                int bodyEnd = content.indexOf(">", bodyStart);
                if (bodyEnd > 0) {
                    content = content.substring(0, bodyEnd + 1) + Util.loadResourceWithPort("online.html", port) + content.substring(bodyEnd + 1);
                }
            }
        }
        super.doAction(basedir, file, content, pattern);
    }


}
