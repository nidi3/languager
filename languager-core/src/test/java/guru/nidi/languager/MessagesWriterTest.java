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

import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static guru.nidi.languager.Message.Status.FOUND;
import static guru.nidi.languager.Message.Status.NOT_FOUND;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class MessagesWriterTest extends BaseTest {
    @Test
    public void testWriteNew() throws Exception {
        MessagesWriter writer = new MessagesWriter(Util.ISO, ',');
        File f = File.createTempFile("pre", "post");
        List<Message> msgs = new ArrayList<>();
        msgs.add(new Message("key2", FOUND, "val,ue2"));
        msgs.add(new Message("key3", NOT_FOUND, "val\"ue3"));
        msgs.add(new Message("key1", FOUND, null));
        writer.write(f, msgs);
        BufferedReader in = Util.reader(f, Util.ISO);
        assertEquals("key,status,occurs,default value,en,de", in.readLine());
        assertEquals("key1,+,,,,", in.readLine());
        assertEquals("key2,+,,\"val,ue2\",,", in.readLine());
        assertEquals("key3,-,,\"val\"\"ue3\",,", in.readLine());
        in.close();
        f.delete();
        f.deleteOnExit();
    }

    @Test
    public void testWriteExisting() throws Exception {
        File base = fromTestDir("");
        File f = File.createTempFile("pre", "post");
        FileUtils.copyFile(new File(base, "existing.csv"), f);

        MessagesWriter writer = new MessagesWriter(Util.ISO, ',');
        List<Message> msgs = new ArrayList<>();
        msgs.add(new Message("key1", FOUND, "val\"ue3"));
        msgs.add(new Message("key3", FOUND, null));
        writer.write(f, msgs);
        BufferedReader in = Util.reader(f, Util.ISO);
        assertEquals("key,status,occurs,default value,en,de", in.readLine());
        assertEquals("key1,+,,\"val\"\"ue3\",value3,", in.readLine());
        assertEquals("key2,-,,,value1,wert2", in.readLine());
        assertEquals("key3,*,,bla,value1,wert3", in.readLine());
        in.close();
        f.delete();
        f.deleteOnExit();
    }
}
