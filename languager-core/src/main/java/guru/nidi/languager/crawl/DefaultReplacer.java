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
package guru.nidi.languager.crawl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 *
 */
public class DefaultReplacer implements Replacer {
    private final String replacement;
    private final String parameterMarker;
    private final String parameterSeparator;
    private final Properties properties;
    private final List<Escape> escapes;

    public DefaultReplacer(String replacement, String parameterMarker, String parameterSeparator, Properties properties, List<Escape> escapes) {
        this.replacement = replacement;
        this.parameterMarker = parameterMarker;
        this.parameterSeparator = parameterSeparator;
        this.properties = properties;
        this.escapes = escapes;
    }

    public String replace(File f, Matcher m) {
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
        List<String> parameters = new ArrayList<>();
        for (int i = 2; i < m.groupCount(); i++) {
            String group = m.group(i);
            if (group == null) {
                parameters.add("");
            } else {
                if (parameterSeparator == null || parameterSeparator.length() == 0) {
                    parameters.add(group);
                } else {
                    Collections.addAll(parameters, group.split(parameterSeparator));
                }
            }
        }
        return parameters;
    }

    protected String value(Matcher m, int group) {
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
}
