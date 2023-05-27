package mugres.live.processor;

public class Status<T> {
    private final String text;
    private final T data;

    private Status(final String text, T data) {
        this.text = text;
        this.data = data;
    }

    public static Status of(final String text) {
        return new Status(text, null);
    }

    public static <X> Status of(final String text, final X data) {
        return new Status(text, data);
    }

    public String text() {
        return text;
    }

    public T data() {
        return data;
    }
}
