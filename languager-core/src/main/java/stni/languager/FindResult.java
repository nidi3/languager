package stni.languager;

/**
 *
 */
public class FindResult<T> {
    private final SourcePosition position;
    private final T finding;

    public FindResult(SourcePosition position, T finding) {
        this.position = position;
        this.finding = finding;
    }

    public SourcePosition getPosition() {
        return position;
    }

    public T getFinding() {
        return finding;
    }
}
