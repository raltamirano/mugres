package mugres.function;

import mugres.common.Length;

public class Result<T> {
    private final T data;
    private final Throwable error;
    private final int measures;

    private Result(final T data, final int measures) {
        if (data == null)
            throw new IllegalArgumentException("data");
        if (measures < 0)
            throw new IllegalArgumentException("measures");

        this.data = data;
        this.measures = measures;
        this.error = null;
    }

    private Result(final Throwable error) {
        if (error == null)
            throw new IllegalArgumentException("error");

        this.data = null;
        this.error = error;
        this.measures = 0;
    }

    public static <X> Result<X> success(final X data, final int measures) {
        return new Result<>(data, measures);
    }

    public static <X> Result<X> error(final Throwable error) {
        return new Result<>(error);
    }

    public T data() {
        return data;
    }

    public int measures() {
        return measures;
    }

    public Throwable error() {
        return error;
    }

    public boolean succeeded() {
        return error == null;
    }
}
