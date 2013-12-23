package stni.languager.crawl;

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
