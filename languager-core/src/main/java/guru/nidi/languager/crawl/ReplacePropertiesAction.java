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
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class ReplacePropertiesAction extends AbstractContentReadingCrawlAction {
    private final Properties properties;
    private final File targetDir;

    public ReplacePropertiesAction(Properties properties, File targetDir) {
        this.properties = properties;
        this.targetDir = targetDir;
    }

    @Override
    protected void doAction(File basedir, File file, String content, CrawlPattern pattern) throws IOException {
        for (Map.Entry<Object, Object> property : properties.entrySet()) {
            content = content.replace((String) property.getKey(), (String) property.getValue());
        }
        File target = target(file, basedir, targetDir);
        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(target, file.getName())), pattern.getEncoding())) {
            out.write(content);
        }
    }

}
