package mugres.core.common;

import java.util.Objects;

public class TimeSignature {
    private int numerator;
    private Value denominator;

    private TimeSignature(final int numerator, final Value denominator) {
        if (numerator <= 0)
            throw new IllegalArgumentException("Time signature numerator must be: 1 <= x <= 128");
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public static TimeSignature of(final int numerator, final Value denominator) {
        return new TimeSignature(numerator, denominator);
    }

    public int getNumerator() {
        return numerator;
    }

    public void setNumerator(int numerator) {
        this.numerator = numerator;
    }

    public Length measuresLength() {
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

    public Value getDenominator() {
        return denominator;
    }

    public void setDenominator(Value denominator) {
        this.denominator = denominator;
    }

    /** 3/4 */
    public static final TimeSignature TS34 = of(3, Value.QUARTER);
    /** 4/4 */
    public static final TimeSignature TS44 = of(4, Value.QUARTER);
    /** 6/8 */
    public static final TimeSignature TS68 = of(6, Value.EIGHTH);
    /** 8/8 */
    public static final TimeSignature TS88 = of(8, Value.EIGHTH);
    /** 12/8 */
    public static final TimeSignature TS128 = of(12, Value.EIGHTH);
}
