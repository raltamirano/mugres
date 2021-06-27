package mugres.core.utils.givers;

import java.util.List;

import static java.util.Arrays.asList;
import static mugres.core.utils.Randoms.RND;

public class Randomized<T> extends AbstractGiver<T> {
    private final List<T> source;

    private Randomized(final List<T> source) {
        if (source == null)
            throw new IllegalArgumentException("source");

        this.source = source;
    }

    public static <X> Randomized<X> of(final X... source) {
        return of(asList(source));
    }

    public static <X> Randomized<X> of(final List<X> source) {
        return new Randomized<>(source);
    }

    @Override
    public T get() {
        return source.isEmpty() ? null : source.get(RND.nextInt(source.size()));
    }
}
