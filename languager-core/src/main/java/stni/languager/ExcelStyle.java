package stni.languager;

/**
 *
 */
public class ExcelStyle {
    private int width;
    private boolean wordWrap;
    private boolean bold;

    public ExcelStyle() {
    }

    public ExcelStyle width(int width) {
        this.width = width;
        return this;
    }

    public ExcelStyle wordWrap(boolean wordWrap) {
        this.wordWrap = wordWrap;
        return this;
    }

    public ExcelStyle bold(boolean bold) {
        this.bold = bold;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public boolean isWordWrap() {
        return wordWrap;
    }

    public boolean isBold() {
        return bold;
    }
}
