package stni.languager.online;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import stni.languager.CsvReader;
import stni.languager.CsvWriter;
import stni.languager.MessageLine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 */
public class LanguagerServer {
    private final Server server;

    public LanguagerServer(int port, File csvFile, String encoding, char separator) throws Exception {
        server = new Server(port);
        server.setHandler(new LanguageServerHandler(port, csvFile, encoding, separator));
    }

    public void start() throws Exception {
        server.start();
        server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }

    private static class LanguageServerHandler extends AbstractHandler {
        private final int port;
        private final File csvFile;
        private final String encoding;
        private final char separator;

        private MessageLine firstLine;
        private final Map<String, MessageLine> messages = new TreeMap<>();
        private final ObjectMapper mapper = new ObjectMapper();

        private LanguageServerHandler(int port, File csvFile, String encoding, char separator) throws IOException {
            this.port = port;
            this.csvFile = csvFile;
            this.encoding = encoding;
            this.separator = separator;
            readCsv();
        }


        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setHeader("Content-Type", "application/javascript");

            final String path = request.getPathInfo();
            if (!serveFile(response, path)) {
                writeResponse(request, response, handle(path));
            }
        }

        private boolean serveFile(HttpServletResponse response, String path) throws IOException {
            if (!path.equals("/online.js")) {
                return false;
            }
            response.getWriter().write(Util.loadResourceWithPort("online.js", port));
            response.getWriter().close();
            return true;
        }

        private Object handle(String path) throws IOException {
            if (path.startsWith("/value/")) {
                return findValues(path.substring(7));
            }

            if (path.startsWith("/setValue/")) {
                String values = path.substring(10);
                int pos = values.indexOf('/');
                setValues(values.substring(0, pos), mapper.readValue(values.substring(pos + 1), Map.class));
                writeCsv();
                return "Written";
            }

            return "invalid path " + path;
        }

        private void writeResponse(HttpServletRequest request, HttpServletResponse response, Object res) throws IOException {
            final StringWriter sw = new StringWriter();
            mapper.writeValue(sw, res);

            final String jsonp = request.getParameter("jsonp");
            final String fullResponse = jsonp + "(" + sw.toString() + ");";

            response.getWriter().print(fullResponse);
            response.getWriter().close();
        }

        private void readCsv() throws IOException {
            try (final CsvReader in = new CsvReader(new InputStreamReader(new FileInputStream(csvFile), encoding), separator)) {
                firstLine = in.readMessageLine();
                while (!in.isEndOfInput()) {
                    final MessageLine line = in.readMessageLine();
                    messages.put(line.readKey(), line);
                }
            }
        }

        private void writeCsv() throws IOException {
            try (CsvWriter out = new CsvWriter(new OutputStreamWriter(new FileOutputStream(csvFile), encoding), separator)) {
                out.writeLine(firstLine);
                for (MessageLine line : messages.values()) {
                    out.writeLine(line);
                }
            }
        }

        private void setValues(String key, Map<String, String> values) throws IOException {
            messages.put(key, messages.get(key).withValues(firstLine, values));
        }

        private Object findValues(String key) {
            final MessageLine line = messages.get(key);
            if (line == null) {
                return "Not found " + key;
            }
            return line.asMap(firstLine);
        }
    }
}
