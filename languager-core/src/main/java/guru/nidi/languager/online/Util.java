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
