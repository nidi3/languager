package stni.languager.check;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import stni.languager.FindResult;
import stni.languager.Logger;
import stni.languager.MessageLine;
import stni.languager.SourcePosition;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static stni.languager.Ansi.ansi;

/**
 *
 */
public class LinkChecker {
    private static final Pattern LINK_PATTERN = Pattern.compile("https?://[^ \"]+");

    private final File file;
    private final List<MessageLine> contents;
    private final Logger logger;
    private final HttpClient client;

    public LinkChecker(File file, List<MessageLine> contents, Logger logger) throws IOException {
        this.file = file;
        this.contents = contents;
        this.logger = logger;

        final PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
        cm.setMaxTotal(20);
        this.client = new DefaultHttpClient(cm);
    }

    public List<FindResult<String>> findBrokenLinks() {
        ExecutorService executor = Executors.newCachedThreadPool();
        final List<FindResult<String>> res = Collections.synchronizedList(new ArrayList<FindResult<String>>());
        final Set<String> urls = new HashSet<String>();
        int lineNum = 1;

        for (MessageLine line : contents.subList(1, contents.size())) {
            lineNum++;
            int col = 1;
            int elemNum = 0;
            for (String element : line) {
                final Matcher matcher = LINK_PATTERN.matcher(element);
                while (matcher.find()) {
                    final String url = matcher.group();
                    if (!urls.contains(url)) {
                        urls.add(url);
                        executor.submit(new LinkValidator(res, url,
                                new SourcePosition(file, 0, 0, lineNum, col + elemNum + matcher.start())));
                    }
                }
                elemNum++;
                col += element.length();
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //ignore
        }
        return res;
    }

    private class LinkValidator implements Runnable {
        private final List<FindResult<String>> results;
        private final String url;
        private final SourcePosition pos;

        private LinkValidator(List<FindResult<String>> results, String url, SourcePosition pos) {
            this.results = results;
            this.url = url;
            this.pos = pos;
        }

        public void run() {
            if (!isLinkValid(url)) {
                results.add(new FindResult<String>(pos, url));
            }
        }

        private boolean isLinkValid(String url) {
            final HttpGet get = new HttpGet(url);
            final HttpResponse response;
            try {
                response = client.execute(get);
                final int statusCode = response.getStatusLine().getStatusCode();
                logger.log(ansi("1F", ansi("2K", "Checked  " + url)));
                return statusCode < 400;
            } catch (IOException e) {
                return false;
            } finally {
                get.releaseConnection();
            }
        }
    }

}