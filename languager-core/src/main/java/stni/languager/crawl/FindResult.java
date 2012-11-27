package stni.languager.crawl;

import java.util.List;

/**
 *
 */
public class FindResult {
    private final String source;
    private final int line;
    private final int column;
    private final List<String> findings;

    public FindResult(String source, int line, int column, List<String> findings) {
        this.source = source;
        this.line = line;
        this.column = column;
        this.findings = findings;
    }

    public String getSource() {
        return source;
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
