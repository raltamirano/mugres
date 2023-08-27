package mugres.common;

import static java.lang.Math.abs;

public enum Note {
    C("C", 0),
    CS("C#", 1),
    D("D", 2),
    DS("D#", 3),
    E("E", 4),
    F("F", 5),
    FS("F#", 6),
    G("G", 7),
    GS("G#", 8),
    A("A", 9),
    AS("A#", 10),
    B("B", 11);

    private final String label;
    private final int number;

    Note(final String label, final int number) {
        this.label = label;
        this.number = number;
    }

    public static Note of(final int number) {
        for(Note note : Note.values())
            if (note.number == number)
                return note;

        throw new IllegalArgumentException("Midi note number must be 0 (C) <= number <= 11 (B)");
    }

    public static Note of(final String label) {
        for(Note note : Note.values())
            if (note.label.equals(label))
                return note;

        throw new IllegalArgumentException("Invalid note: " + label);
    }

    public Note up(final Interval interval) {
        return up(interval.semitonesFromRoot());
    }

    public Note up(final int semitones) {
        return of((number + semitones) % 12);
    }

    public Note down(final Interval interval) {
        return down(interval.semitonesFromRoot());
    }

    public Note down(final int semitones) {
        return of(abs(((number + 12) - semitones) % 12));
    }

    public String label() {
        return label;
    }

    public int number() {
        return number;
    }

    public Pitch pitch() {
        return pitch(BASE_OCTAVE);
    }

    public Pitch pitch(final int octave) {
        return Pitch.of(this, octave);
    }

    public boolean before(final Note other) {
        return number < other.number;
    }

    public boolean after(final Note other) {
        return number > other.number;
    }

    @Override
    public String toString() {
        return label;
    }

    public static final int BASE_OCTAVE = 3;
}