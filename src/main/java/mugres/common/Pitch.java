package mugres.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.log;
import static mugres.common.MIDI.isValidNote;

public class Pitch implements Comparable<Pitch> {
    private final int midi;
    private final Note note;
    private final int octave;

    private Pitch(final int midi, final Note note, final int octave) {
        if (!isValidNote(midi))
            throw new IllegalArgumentException("Invalid Midi note number: " + midi);

        this.midi = midi;
        this.note = note;
        this.octave = octave;
    }

    public synchronized static Pitch of(final int midi) {
        if (!CACHE.containsKey(midi))
            CACHE.put(midi, new Pitch(midi, Note.of(midi % 12), (midi / 12) - 2));

        return CACHE.get(midi);
    }

    public static Pitch of(final Note note, final int octave) {
        return of(note.number() + ((octave + 2) * 12));
    }

    public static Pitch of(final String input) {
        try {
            return of(Integer.parseInt(input));
        } catch (final NumberFormatException e) {
            final Matcher matcher = PITCH.matcher(input);
            if (!matcher.matches())
                throw new IllegalArgumentException("Invalid pitch: " + input);
            final Note root = Note.of(matcher.group(1));
            final int octave = matcher.group(2) == null ? BASE_OCTAVE : Integer.valueOf(matcher.group(3).trim());
            return of(root, octave);
        }
    }

    public static Pitch of(final double hz) {
        final int midi = (int) ((12 * log(hz / 220.0) / log(2.0)) + 57.01);
        return of(midi);
    }

    public Pitch up(final Interval interval) {
        return up(interval.semitonesFromRoot());
    }

    public Pitch up(final int semitones) {
        if (semitones < 0)
            throw new IllegalArgumentException("semitones can't be a negative number");

        return of(this.midi + semitones);
    }

    public Pitch down(final Interval interval) {
        return down(interval.semitonesFromRoot());
    }

    public Pitch down(final int semitones) {
        if (semitones < 0)
            throw new IllegalArgumentException("semitones can't be a negative number");

        return of(this.midi - semitones);
    }

    public Pitch transpose(final int semitones) {
        return of(this.midi + semitones);
    }

    public Pitch safeTranspose(final int semitones) {
        final int newMidi = midi + semitones;
        return isValidNote(newMidi) ? of(newMidi) : this;
    }

    public int midi() {
        return midi;
    }

    public Note note() {
        return note;
    }

    public int octave() {
        return octave;
    }

    public double hz() {
        return 440.0  * Math.pow(2.0, (midi-69.0)/12.0);
    }

    @Override
    public String toString() {
        return String.format("[%-4s][%03d]",
                String.format("%s%s", note.label(), octave),
                midi);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pitch pitch = (Pitch) o;
        return midi == pitch.midi;
    }

    @Override
    public int hashCode() {
        return Objects.hash(midi);
    }

    @Override
    public int compareTo(final Pitch o) {
        return Integer.compare(this.midi, o.midi);
    }

    private static final Map<Integer, Pitch> CACHE = new HashMap<>();
    private static final Pattern PITCH = Pattern.compile("((?:C|D|E|F|G|A|B)#?)(\\[(-?\\d)\\])?");

    public static final int BASE_OCTAVE = 3;
    public static final Pitch MIDDLE_C = of(Note.C, BASE_OCTAVE);
    public static final Pitch CONCERT_PITCH = of(Note.A, 3);
    public static final int DEFAULT_VELOCITY = 100;
}
