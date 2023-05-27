package mugres.utils.givers;

public class Unique<T> extends AbstractGiver<T> {
    private final T source;

    private Unique(final T source) {
        this.source = source;
    }

    public static <X> Unique<X> of(final X source) {
        return new Unique<>(source);
    }

    @Override
    public T get() {
        return source;
    }
}
