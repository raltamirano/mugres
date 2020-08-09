package mugres.core.common.chords;

import mugres.core.common.Length;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChordProgression {
    private final int measures;
    private final List<ChordEvent> events = new ArrayList<>();

    private ChordProgression(final int measures) {
        this.measures = measures;
    }

    public static ChordProgression of(final int measures) {
        return new ChordProgression(measures);
    }

    public ChordProgression event(final Chord chord, final Length at) {
        events.add(new ChordEvent(chord, at));
        return this;
    }

    public ChordProgression event(final Chord chord, final Length at, final int octave) {
        events.add(new ChordEvent(chord, at,octave));
        return this;
    }

    public int getMeasures() {
        return measures;
    }

    public List<ChordEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public static class ChordEvent {
        private final Chord chord;
        private final Length position;
        private final Integer octave;

        private ChordEvent(final Chord chord, final Length position) {
            this.chord = chord;
            this.position = position;
            this.octave = null;
        }

        private ChordEvent(final Chord chord, final Length position, final int octave) {
            this.chord = chord;
            this.position = position;
            this.octave = octave;
        }

        public Chord getChord() {
            return chord;
        }

        public Length getPosition() {
            return position;
        }

        public Integer getOctave() {
            return octave;
        }

        public String notation() {
            return chord.notation() + (octave != null  ? " [" + octave + "]" : "");
        }
    }
}
