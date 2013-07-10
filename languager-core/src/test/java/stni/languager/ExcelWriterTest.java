package stni.languager;

import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 */
public class ExcelWriterTest {
    @Test
    public void simple() throws IOException {
        //ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileOutputStream out = new FileOutputStream("out.xlsx");
        ExcelWriter writer = new ExcelWriter(out, Arrays.asList(new ExcelStyle().width(4).wordWrap(true)), Arrays.asList(new ExcelStyle().bold(true)));
        writer.writeField("a1");
        writer.newLine();
        writer.writeLine(Arrays.asList("a2", "b2"));
        writer.close();
    }
}
