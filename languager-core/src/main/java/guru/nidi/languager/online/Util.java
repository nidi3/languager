package guru.nidi.languager.online;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
class Util {
    private Util() {
    }

    static String loadResource(String name) throws IOException {
        try (final InputStream in = Util.class.getResourceAsStream(name)) {
            byte[] buf = new byte[in.available()];
            int read = in.read(buf);
            return new String(buf, 0, read, "utf-8");
        }
    }

    static String loadResourceWithPort(String name, int port) throws IOException {
        return loadResource(name).replace(":8880", ":" + port);
    }

    static boolean isOnline(File f) {
        return f.getName().toLowerCase().endsWith(".html");
    }
}
