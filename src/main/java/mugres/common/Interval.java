package mugres.common;

public enum Interval {
    UNISON("Unison", "1", 0),
    MINOR_SECOND("Minor 2nd", "b2",  1),
    MAJOR_SECOND("Major 2nd", "2", 2),
    MINOR_THIRD("Minor 3rd", "b3", 3),
    MAJOR_THIRD("Major 3rd", "3", 4),
    PERFECT_FOURTH("Perfect 4th", "4", 5),
    AUGMENTED_FOURTH("Augmented 4th", "#4", 6),
    DIMINISHED_FIFTH("Diminished 5th", "b5", 6),
    PERFECT_FIFTH("Perfect 5th", "5", 7),
    MINOR_SIXTH("Minor 6th", "b6", 8),
    MAJOR_SIXTH("Major 6th", "6", 9),
    DIMINISHED_SEVENTH("Diminished 7th", "bb7", 9),
    MINOR_SEVENTH("Minor 7th", "b7", 10),
    MAJOR_SEVENTH("Major 7th", "7", 11),
    OCTAVE("Octave", "8", 12);

    private final String label;
    private final String shortName;
    private final int semitonesFromRoot;

    Interval(final String label, final String shortName,
             final int semitonesFromRoot) {
        this.label = label;
        this.shortName = shortName;
        this.semitonesFromRoot = semitonesFromRoot;
    }

    public String label() {
        return label;
    }

    public String shortName() {
        return shortName;
    }

    public int semitonesFromRoot() {
        return semitonesFromRoot;
    }

    public static Interval forShortName(final String shortName) {
        for(Interval interval : values())
            if (interval.shortName.equals(shortName))
                return interval;

        throw new IllegalArgumentException("Invalid interval with short name: " + shortName);
    }

}
