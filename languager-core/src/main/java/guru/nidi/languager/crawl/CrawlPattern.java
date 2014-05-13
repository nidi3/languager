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

/**
 *
 */
public class CrawlPattern {
    private static final String DEFAULT_ENCODING = "utf-8";

    private final File basedir;
    private final String includes;
    private final String excludes;
    private final String encoding;

    public CrawlPattern(File basedir, String includes, String excludes, String encoding) {
        this.basedir = basedir;
        this.includes = includes;
        this.excludes = excludes;
        this.encoding = encoding != null ? encoding : DEFAULT_ENCODING;
    }

    public File getBasedir() {
        return basedir;
    }

    public String getIncludes() {
        return includes;
    }

    public String getExcludes() {
        return excludes;
    }

    public String getEncoding() {
        return encoding;
    }

}
