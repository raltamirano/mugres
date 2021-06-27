package mugres.core.utils.givers;

import java.util.List;

import static java.util.Arrays.asList;

public class Sequential<T> extends AbstractGiver<T> {
    private int counter;
    private final List<T> source;

    private Sequential(final List<T> source) {
        if (source == null)
            throw new IllegalArgumentException("source");

        this.source = source;
    }

    public static <X> Sequential<X> of(final X... source) {
        return of(asList(source));
    }

    public static <X> Sequential<X> of(final List<X> source) {
        return new Sequential<>(source);
    }

    @Override
    public T get() {
        return source.isEmpty() ? null : source.get(counter++ % source.size());
    }
}
