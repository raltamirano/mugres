package mugres.core.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Pitch {
    private final int midi;
    private final Note note;
    private final int octave;

    private Pitch(final int midi, final Note note, final int octave) {
        this.midi = midi;
        this.note = note;
        this.octave = octave;
    }

    public Pitch up(final Interval interval) {
        return up(interval.semitonesFromRoot());
    }

    public Pitch up(final int semitones) {
        final int targetNote = this.midi + semitones;

        if (!isValidMidiNoteNumber(midi))
            throw new IllegalArgumentException("Invalid MIDI note number: " + targetNote);

        return of(targetNote);
    }

    public Pitch down(final Interval interval) {
        return down(interval.semitonesFromRoot());
    }

    public Pitch down(final int semitones) {
        return up(-semitones);
    }

    public int getMidi() {
        return midi;
    }

    public Note getNote() {
        return note;
    }

    public int getOctave() {
        return octave;
    }

    public synchronized static Pitch of(final int midi) {
        if (!isValidMidiNoteNumber(midi))
            throw new IllegalArgumentException("Invalid MIDI note number: " + midi);

        if (!CACHE.containsKey(midi))
            CACHE.put(midi, new Pitch(midi, Note.of(midi % 12), (midi / 12) - 2));

        return CACHE.get(midi);
    }

    public static boolean isValidMidiNoteNumber(final int noteNumber) {
        return noteNumber >= 0 && noteNumber <= 127;
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

    private static final Map<Integer, Pitch> CACHE = new HashMap<>();
}
