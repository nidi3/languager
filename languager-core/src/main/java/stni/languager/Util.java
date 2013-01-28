package stni.languager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

    public static void closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

    public static List<List<String>> readCsvFile(File file, String encoding, char separator) throws IOException {
        final List<List<String>> res = new ArrayList<List<String>>();
        CsvReader in = null;
        try {
            in = new CsvReader(new InputStreamReader(new FileInputStream(file), encoding), separator);
            while (!in.isEndOfInput()) {
                res.add(in.readLine());
            }
            return res;
        } finally {
            Util.closeSilently(in);
        }
    }
}
