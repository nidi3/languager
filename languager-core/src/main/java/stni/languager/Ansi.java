package stni.languager;

/**
 *
 */
public class Ansi {
    public static String ansi(String command) {
        return (char) 27 + "[" + command;
    }

    public static String ansi(String command, String rest) {
        return (char) 27 + "[" + command + rest;
    }
}
