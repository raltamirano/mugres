package mugres.common;

import java.util.Objects;

/**
 Lengths are measured as pulses per quarter note (PPQN). 1 quarter note = {@link #PPQN} pulses
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

    public int length() {
        return n;
    }

    public long toMillis(final int bpm) {
        return Math.round((60000.0 / (float)bpm) * n);
    }

    public Length plus(final Value value) {
        return plus(value.length());
    }

    public Length minus(final Length other) {
        return new Length(this.n - other.n);
    }

    public Length plus(final Length other) {
        return new Length(this.n + other.n);
    }

    public Length multiply(final int factor) {
        return new Length(this.n * factor);
    }

    public Length divide(final int factor) {
        return new Length(this.n / factor);
    }

    public Length remainder(final int factor) {
        return new Length(this.n % factor);
    }

    /** Greater than */
    public boolean greaterThan(final Length other) {
        return this.n > other.n;
    }

    public boolean greaterThanOrEqual(final Length other) {
        return this.n >= other.n;
    }

    /** Less than */
    public boolean lessThan(final Length other) {
        return this.n < other.n;
    }

    public boolean lessThanOrEqual(final Length other) {
        return this.equals(other) || this.lessThan(other);
    }

    public Length mod(final Length other) {
        return new Length(this.n % other.n);
    }

    public boolean isEmpty() {
        return n == 0;
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

    /**
     * Pulses Per Quarter Note
     */
    public static final int PPQN = 480;

    /**
     * Quarter Note Length
     */
    public static final Length QUARTER = Length.of(PPQN);
}
