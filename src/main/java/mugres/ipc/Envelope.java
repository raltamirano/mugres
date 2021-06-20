package mugres.ipc;

/**
 * MUGRES IPC envelope
 *
 * @param <T> The type for the payload of this envelope
 */
public class Envelope<T> {
    private final Header header;
    private final T payload;

    public Envelope(final Header header, final T payload) {
        if (header == null)
            throw new IllegalArgumentException("header");
        if (payload == null)
            throw new IllegalArgumentException("payload");

        this.header = header;
        this.payload = payload;
    }

    public static <X> Envelope<X> of(final Header header, final X payload) {
        return new Envelope<>(header, payload);
    }

    public Header header() {
        return header;
    }

    public T payload() {
        return payload;
    }
}
