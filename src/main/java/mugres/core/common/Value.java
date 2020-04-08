package mugres.core.common;

public enum Value {
    WHOLE("whole", Length.of(64), "w"),
    HALF("half", Length.of(32), "h"),
    QUARTER("quarter", Length.of(16), "q"),
    EIGHTH("8th", Length.of(8), "e"),
    SIXTEENTH("16th", Length.of(4), "s"),
    THIRTY_SECOND("32th", Length.of(2), "t"),
    SIXTY_FOURTH("64th", Length.of(1), "m");

    private final String label;
    private final Length length;
    private final String id;

    Value(final String label, final Length length, final String id) {
        this.label = label;
        this.length = length;
        this.id = id;
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

    public String id() {
        return id;
    }
}
