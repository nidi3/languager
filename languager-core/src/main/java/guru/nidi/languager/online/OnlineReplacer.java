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
