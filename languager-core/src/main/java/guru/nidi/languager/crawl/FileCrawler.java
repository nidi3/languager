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

import guru.nidi.languager.Util;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class FileCrawler {

    private final CrawlPattern crawlPattern;

    public FileCrawler(CrawlPattern crawlPattern) {
        this.crawlPattern = crawlPattern;
    }

    public <T extends CrawlAction> T crawl(T crawlAction) throws IOException {
        List<File> files = Util.getFiles(crawlPattern.getBasedir(), crawlPattern.getIncludes(), crawlPattern.getExcludes());
        for (File file : files) {
            crawlAction.action(crawlPattern.getBasedir(), file, crawlPattern);
        }
        return crawlAction;
    }

}
