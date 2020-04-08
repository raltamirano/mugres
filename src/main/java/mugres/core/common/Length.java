package mugres.core.common;

/**
 Lengths are measured with Value.SIXTY_FOURTH as the unit.
 */
public class Length {
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

    public static final Length ZERO = Length.of(0);

}
