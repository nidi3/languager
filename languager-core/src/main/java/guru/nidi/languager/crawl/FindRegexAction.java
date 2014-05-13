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

import guru.nidi.languager.FindResult;
import guru.nidi.languager.SourcePosition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static guru.nidi.languager.crawl.FindRegexAction.Flag.TRIM;
import static guru.nidi.languager.crawl.FindRegexAction.Flag.WITH_EMPTY;

/**
 *
 */
public class FindRegexAction extends AbstractContentReadingCrawlAction {
    public enum Flag {
        WITH_EMPTY, TRIM
    }

    private final List<FindResult<List<String>>> results = new ArrayList<>();

    private final Pattern regex;
    private final Pattern ignoreRegex;
    private final EnumSet<Flag> flags;

    public FindRegexAction(String regex, String ignoreRegex, EnumSet<Flag> flags) {
        this.regex = Pattern.compile(regex, Pattern.DOTALL);
        this.ignoreRegex = ignoreRegex == null ? null : Pattern.compile(ignoreRegex, Pattern.DOTALL);
        if (regex.indexOf('(') < 0 || regex.indexOf(')') < 0) {
            throw new IllegalArgumentException("Regex must contain at least one group");
        }
        this.flags = flags == null ? EnumSet.noneOf(Flag.class) : flags;
    }

    @Override
    protected void doAction(File basedir, File file, String content, CrawlPattern pattern) throws IOException {
        Matcher matcher = regex.matcher(content);
        while (matcher.find()) {
            if (isValidMatch(matcher)) {
                List<String> finds = new ArrayList<>();
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    finds.add(group(matcher, i));
                }
                results.add(new FindResult<>(
                        new SourcePosition(
                                file, matcher.start(), matcher.end(),
                                lineOfPosition(matcher.start()), columnOfPosition(matcher.start())),
                        finds));
            }
        }
    }

    protected boolean isValidMatch(Matcher matcher) {
        return checkEmpty(matcher) && checkIgnore(matcher);
    }

    private boolean checkEmpty(Matcher matcher) {
        return flags.contains(WITH_EMPTY) || group(matcher, 1).length() > 0;
    }

    private boolean checkIgnore(Matcher matcher) {
        return (ignoreRegex == null || !ignoreRegex.matcher(group(matcher, 1)).matches());
    }

    protected String group(Matcher m, int index) {
        final String group = m.group(index);
        return flags.contains(TRIM) ? group.trim() : group;
    }

    public List<FindResult<List<String>>> getResults() {
        return results;
    }

    public Pattern getRegex() {
        return regex;
    }

    public EnumSet<Flag> getFlags() {
        return flags;
    }
}
