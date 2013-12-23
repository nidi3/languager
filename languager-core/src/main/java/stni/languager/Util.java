package stni.languager;

import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Util {
    final static String ISO = "iso-8859-1";

    static BufferedReader reader(File file, String encoding) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
    }

    static BufferedWriter writer(File file, String encoding) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
    }

    public static List<MessageLine> readCsvFile(File file, String encoding, char separator) throws IOException {
        final List<MessageLine> res = new ArrayList<>();
        try (CsvReader in = new CsvReader(new InputStreamReader(new FileInputStream(file), encoding), separator)) {
            while (!in.isEndOfInput()) {
                res.add(MessageLine.of(in.readLine()));
            }
            return res;
        }
    }

    public static List<File> getFiles(File dir, String includes, String excludes) throws IOException {
        @SuppressWarnings("unchecked")
        List<File> files = FileUtils.getFiles(dir, includes, excludes);
        return files;
    }

}
