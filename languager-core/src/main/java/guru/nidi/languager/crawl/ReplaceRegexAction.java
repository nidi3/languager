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
        super(regex, null, flags);
        this.parameter = parameter;
    }

    @Override
    protected void doAction(File basedir, File file, String content, CrawlPattern pattern) throws IOException {
        Matcher matcher = getRegex().matcher(content);
        StringBuffer s = new StringBuffer();
        while (matcher.find()) {
            if (isValidMatch(matcher)) {
                matcher.appendReplacement(s, parameter.getReplacer().replace(file,matcher));
            }
        }
        matcher.appendTail(s);
        File target = target(file, basedir, parameter.getTargetDir());
        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(target, file.getName())), pattern.getEncoding())) {
            out.write(s.toString());
        }
    }

}
