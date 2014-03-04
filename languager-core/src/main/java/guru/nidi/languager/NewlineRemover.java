package guru.nidi.languager;

/**
 *
 */
public class NewlineRemover implements Message.Transformer {
    public String transform(String lang, String value) {
        return value.replaceAll("\\r?\\n\\s*", " ").trim();
    }
}
