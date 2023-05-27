package mugres.common;

import static mugres.common.Length.PPQN;

public enum Value {
    WHOLE("whole", Length.of(PPQN * 4), "w", 1),
    HALF("half", Length.of(PPQN * 2), "h", 2),
    QUARTER("quarter", Length.of(PPQN), "q", 4),
    EIGHTH("8th", Length.of(PPQN/2), "e", 8),
    SIXTEENTH("16th", Length.of(PPQN/4), "s", 16),
    THIRTY_SECOND("32th", Length.of(PPQN/8), "t", 32),
    SIXTY_FOURTH("64th", Length.of(PPQN/16), "m", 64);

    private final String label;
    private final Length length;
    private final String id;
    private final int denominator;

    Value(final String label, final Length length, final String id, final int denominator) {
        this.label = label;
        this.length = length;
        this.id = id;
        this.denominator = denominator;
    }

    public static Value of(final String id) {
        for(Value v : values())
            if (v.id.equals(id))
                return v;
        throw new IllegalArgumentException("Invalid value ID: " + id);
    }

    public static Value forLength(final Length length) {
        for(Value v : values())
            if (v.length.equals(length))
                return v;
        throw new IllegalArgumentException("Invalid value for length: " + length);
    }

    public String label() {
        return label;
    }

    public Length length() {
        return length;
    }

    public int denominator() {
        return denominator;
    }

    public String id() {
        return id;
    }
}
