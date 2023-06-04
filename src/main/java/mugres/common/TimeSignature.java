package mugres.common;

import java.util.Objects;

public class TimeSignature {
    private final int numerator;
    private final Value denominator;

    private TimeSignature(final int numerator, final Value denominator) {
        if (numerator <= 0)
            throw new IllegalArgumentException("Time signature numerator must be: 1 <= x <= 128");
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public static TimeSignature of(final int numerator, final Value denominator) {
        return new TimeSignature(numerator, denominator);
    }

    public static TimeSignature of(final String timeSignature) {
        if ("3/4".equals(timeSignature)) return TS34;
        if ("4/4".equals(timeSignature)) return TS44;
        if ("6/8".equals(timeSignature)) return TS68;
        if ("7/8".equals(timeSignature)) return TS78;
        if ("8/8".equals(timeSignature)) return TS88;
        if ("12/8".equals(timeSignature)) return TS128;

        throw new RuntimeException("Not implemented: parsing of time signatures!");
    }

    public int numerator() {
        return numerator;
    }

    public Value denominator() {
        return denominator;
    }

    /**
     * Length of a single measure of this time signature
     * @return
     */
    public Length measureLength() {
        return measuresLength(1);
    }

    public Length measuresLength(final int measures) {
        return denominator.length().multiply(measures * numerator);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSignature that = (TimeSignature) o;
        return numerator == that.numerator &&
                denominator == that.denominator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    @Override
    public String toString() {
        return String.format("%d/%d", numerator, denominator.denominator());
    }

    /** 3/4 */
    public static final TimeSignature TS34 = of(3, Value.QUARTER);
    /** 4/4 */
    public static final TimeSignature TS44 = of(4, Value.QUARTER);
    /** 6/8 */
    public static final TimeSignature TS68 = of(6, Value.EIGHTH);
    /** 7/8 */
    public static final TimeSignature TS78 = of(7, Value.EIGHTH);
    /** 8/8 */
    public static final TimeSignature TS88 = of(8, Value.EIGHTH);
    /** 12/8 */
    public static final TimeSignature TS128 = of(12, Value.EIGHTH);

    private static final TimeSignature[] COMMON_TIME_SIGNATURES = new TimeSignature[] {
            TS34,
            TS44,
            TS68,
            TS78,
            TS88,
            TS128
    };

    public static TimeSignature[] commonTimeSignatures() {
        return COMMON_TIME_SIGNATURES;
    }
}
