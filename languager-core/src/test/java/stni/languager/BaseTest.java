package stni.languager;

import java.io.File;

/**
 *
 */
public class BaseTest {

    private static final String PATH = "src/test/resources/stni/languager/";

    protected File fromBaseDir(String relativeToBase) {
        File base = new File("languager-core");
        if (!base.exists()) {
            base = new File("");
        }
        return new File(base, relativeToBase);
    }

    protected File fromTestDir(String relativeToTest) {
        return fromBaseDir(PATH + relativeToTest);
    }
}
