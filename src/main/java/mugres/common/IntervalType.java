package mugres.common;

public enum IntervalType {
    UNISON(0),
    SECOND(1),
    THIRD(2),
    FOURTH(3),
    FIFTH(4),
    SIXTH(5),
    SEVENTH(6),
    OCTAVE(7);

    private final int scaleSteps;

    IntervalType(final int scaleSteps) {
        this.scaleSteps = scaleSteps;
    }

    public int scaleSteps() {
        return scaleSteps;
    }
}
