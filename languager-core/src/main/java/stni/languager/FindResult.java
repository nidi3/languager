package stni.languager;

import java.util.List;

/**
 *
 */
public class FindResult {
    private final SourcePosition position;
    private final List<String> findings;

    public FindResult(SourcePosition position, List<String> findings) {
        this.position = position;
        this.findings = findings;
    }

    public SourcePosition getPosition() {
        return position;
    }

    public List<String> getFindings() {
        return findings;
    }
}
