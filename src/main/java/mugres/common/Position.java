package mugres.common;

public class Position {
    private final int measure;
    private final int beat;

    private Position(final int measure, final int beat) {
        if (measure < 1)
            throw new IllegalArgumentException("measure");
        if (beat < 1)
            throw new IllegalArgumentException("beat");

        this.measure = measure;
        this.beat = beat;
    }

    public static Position of(final int measure, final int beat) {
        return new Position(measure, beat);
    }

    public int measure() {
        return measure;
    }

    public int beat() {
        return beat;
    }

    public Length asLength(final Context context) {
        if (beat > context.timeSignature().numerator())
            throw new IllegalArgumentException("Incompatible Time Signature!");

        final int distanceInBeats = ((measure - 1) * context.timeSignature().numerator()) + (beat - 1);
        return context.timeSignature().beatsLength(distanceInBeats);
    }
}
