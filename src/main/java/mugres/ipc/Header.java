package mugres.ipc;

/**
 * Envelope header
 */
public class Header {
    private final String from;
    private final String to;

    public Header(final String from, final String to) {
        if (from == null)
            throw new IllegalArgumentException("from");
        if (to == null)
            throw new IllegalArgumentException("to");

        this.from = from;
        this.to = to;
    }

    public static Header of(final String from, final String to) {
        return new Header(from, to);
    }

    public String from() {
        return from;
    }

    public String to() {
        return to;
    }
}
