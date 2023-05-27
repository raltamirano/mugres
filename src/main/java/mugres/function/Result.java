package mugres.function;

public class Result<T> {
    private final T data;
    private final Throwable error;

    public Result(final T data) {
        if (data == null)
            throw new IllegalArgumentException("data");

        this.data = data;
        this.error = null;
    }

    public Result(final Throwable error) {
        if (error == null)
            throw new IllegalArgumentException("error");

        this.data = null;
        this.error = error;
    }

    public T data() {
        return data;
    }

    public Throwable error() {
        return error;
    }

    public boolean succeeded() {
        return error == null;
    }
}
