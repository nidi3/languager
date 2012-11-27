package stni.languager;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

/**
 *
 */
public class MessagesWriterTest extends BaseTest {
    @Test
    public void testWriteNew() throws Exception {
        MessagesWriter writer = new MessagesWriter(Util.ISO, ',');
        File f = File.createTempFile("pre", "post");
        List<Message> msgs = new ArrayList<Message>();
        msgs.add(new Message("key2", true, "val,ue2"));
        msgs.add(new Message("key3", false, "val\"ue3"));
        msgs.add(new Message("key1", true, null));
        writer.write(f, msgs);
        BufferedReader in = Util.reader(f, Util.ISO);
        assertEquals("key,unknown,default value,en,de", in.readLine());
        assertEquals("key1,,,,", in.readLine());
        assertEquals("key2,,\"val,ue2\",,", in.readLine());
        assertEquals("key3,*,\"val\"\"ue3\",,", in.readLine());
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
        List<Message> msgs = new ArrayList<Message>();
        msgs.add(new Message("key1", true, "val\"ue3"));
        msgs.add(new Message("key3", true, null));
        writer.write(f, msgs);
        BufferedReader in = Util.reader(f, Util.ISO);
        assertEquals("key,unknown,default value,en,de", in.readLine());
        assertEquals("key1,,\"val\"\"ue3\",value3,", in.readLine());
        assertEquals("key2,*,,value1,wert1",in.readLine());
        assertEquals("key3,,bla,value1,wert1", in.readLine());
        in.close();
        f.delete();
        f.deleteOnExit();
    }
}
