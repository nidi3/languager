/*
 * Copyright (C) 2014 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.languager.online;

import guru.nidi.languager.crawl.CrawlPattern;
import guru.nidi.languager.crawl.ReplaceRegexAction;
import guru.nidi.languager.crawl.ReplaceRegexActionParameter;

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
