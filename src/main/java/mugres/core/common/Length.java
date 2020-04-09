package mugres.core.common;

import java.util.Objects;

/**
 Lengths are measured with Value.SIXTY_FOURTH as the unit.
 */
public class Length implements Comparable<Length> {
    private final int n;

    private Length(final int n) {
        if (n < 0)
            throw new IllegalArgumentException("Lengths must be > 0");

        this.n = n;
    }

    public static Length of(final int n) {
        return new Length(n);
    }

    public int getLength() {
        return n;
    }

    public long toMillis(final int bpm) {
        return Math.round((60000.0 / bpm / 16.0) * n);
    }

    public Length plus(final Length other) {
        return new Length(this.n + other.n);
    }

    public Length multiply(final int factor) {
        return new Length(this.n * factor);
    }

    public static final Length ZERO = Length.of(0);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Length length = (Length) o;
        return n == length.n;
    }

    @Override
    public int hashCode() {
        return Objects.hash(n);
    }

    @Override
    public int compareTo(Length o) {
        return Long.compare(this.n, o.n);
    }

    @Override
    public String toString() {
        return String.valueOf(n);
    }

}
