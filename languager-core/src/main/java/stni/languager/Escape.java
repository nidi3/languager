package stni.languager;

/**
 * Created with IntelliJ IDEA.
 * User: nidi
 * Date: 16.09.12
 * Time: 17:11
 * To change this template use File | Settings | File Templates.
 */
public class Escape {
    private String from;
    private String to;

    public Escape() {
    }

    public Escape(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
