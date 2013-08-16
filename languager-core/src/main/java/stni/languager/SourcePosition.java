package stni.languager;

import java.io.File;

/**
 *
 */
public class SourcePosition implements Comparable<SourcePosition>{
    private final File source;
    private final int start;
    private final int end;
    private final int line;
    private final int column;

    public SourcePosition(File source, int start, int end, int line, int column) {
        this.source = source;
        this.start = start;
        this.end = end;
        this.line = line;
        this.column = column;
    }

    public File getSource() {
        return source;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return getSource().getAbsolutePath() + ":[" + getLine() + "," + getColumn() + "]";
    }

    @Override
    public int compareTo(SourcePosition o) {
        return source.getName().compareTo(o.source.getName());
    }
}
