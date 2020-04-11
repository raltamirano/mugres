package mugres.core.common;

public enum Value {
    WHOLE("whole", Length.of(64), "w", 1),
    HALF("half", Length.of(32), "h", 2),
    QUARTER("quarter", Length.of(16), "q", 4),
    EIGHTH("8th", Length.of(8), "e", 8),
    SIXTEENTH("16th", Length.of(4), "s", 16),
    THIRTY_SECOND("32th", Length.of(2), "t", 32),
    SIXTY_FOURTH("64th", Length.of(1), "m", 64);

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

    public static Value forId(final String id) {
        for(Value v : values())
            if (v.id.equals(id))
                return v;
        throw new IllegalArgumentException("Invalid value ID: " + id);
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
