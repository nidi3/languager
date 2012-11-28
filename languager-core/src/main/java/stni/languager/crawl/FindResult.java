package stni.languager.crawl;

import java.util.List;

/**
 *
 */
public class FindResult {
    private final String source;
    private final int start;
    private final int end;
    private final int line;
    private final int column;
    private final List<String> findings;

    public FindResult(String source, int start, int end, int line, int column, List<String> findings) {
        this.source = source;
        this.start = start;
        this.end = end;
        this.line = line;
        this.column = column;
        this.findings = findings;
    }

    public String getSource() {
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

    public List<String> getFindings() {
        return findings;
    }
}