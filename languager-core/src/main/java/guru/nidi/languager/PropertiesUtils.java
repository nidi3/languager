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
package guru.nidi.languager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class PropertiesUtils {
    private static final Pattern TEXT_FORMAT_PARAMETER = Pattern.compile("\\{\\d(,.+?)?\\}");
    private static final Pattern SINGLE_SIMPLE_QUOTE = Pattern.compile("([^'])'([^'])");

    private PropertiesUtils() {
    }

    public static String escapeSingleQuotes(String s, boolean always) {
        return isTextFormat(s, always)
                ? SINGLE_SIMPLE_QUOTE.matcher(s).replaceAll("$1''$2")
                : s;
    }

    private static boolean isTextFormat(String s, boolean always) {
        return always || TEXT_FORMAT_PARAMETER.matcher(s).find();
    }

    public static int findFirstSingleQuote(String s, boolean always) {
        Matcher quoteMatcher = SINGLE_SIMPLE_QUOTE.matcher(s);
        return (isTextFormat(s, always) && quoteMatcher.find())
                ? quoteMatcher.start()+1
                : -1;
    }
}
