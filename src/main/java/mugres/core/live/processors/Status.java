package mugres.core.live.processors;

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

    public String getText() {
        return text;
    }

    public T getData() {
        return data;
    }
}
