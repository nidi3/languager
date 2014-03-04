package guru.nidi.languager;

import java.util.Comparator;

/**
 *
 */
public class FindResult<T> {
    public static final Comparator<FindResult<?>> POSITION_COMPARATOR = new Comparator<FindResult<?>>() {
        public int compare(FindResult<?> result1, FindResult<?> result2) {
            int res = result1.getPosition().getSource().compareTo(result2.getPosition().getSource());
            if (res == 0) {
                res = result1.getPosition().getLine() - result2.getPosition().getLine();
            }
            return res;
        }
    };

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
