package mugres.core.common.chords;

import mugres.core.common.Context;
import mugres.core.common.Length;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class ChordProgression {
    private final Context context;
    private final int measures;
    private final Length length;
    private final Map<Length, ChordEvent> events = new TreeMap<>();

    private ChordProgression(final Context context, final int measures) {
        if (context == null)
            throw new IllegalArgumentException("context");
        if (measures <= 0)
            throw new IllegalArgumentException("measures must be > 0!");

        this.context = context;
        this.measures = measures;
        length = context.timeSignature().measuresLength(measures);
    }

    public static ChordProgression of(final Context context, final int measures) {
        return new ChordProgression(context, measures);
    }

    public ChordProgression event(final Chord chord, final Length at) {
        if (at.greaterThan(length))
            throw new IllegalArgumentException("Chord event position exceeds chord progression length!");

        final ChordEvent chordEvent = new ChordEvent(chord, at);
        events.put(at, chordEvent);
        return this;
    }

    public Chord chordAt(final Length position) {
        if (position.greaterThan(length))
            throw new IllegalArgumentException("Position outside of chord progression!");

        if (events.isEmpty())
            return null;

        for(final Map.Entry<Length, ChordEvent> entry : events.entrySet())
            if (position.greaterThanOrEqual(entry.getKey()))
                return entry.getValue().chord();

        throw new RuntimeException("Internal error getting chord from chord progression");
    }

    public int measures() {
        return measures;
    }

    public Length length() {
        return length;
    }

    public Map<Length, ChordEvent> events() {
        return Collections.unmodifiableMap(events);
    }

    public static class ChordEvent {
        private final Chord chord;
        private final Length position;

        private ChordEvent(final Chord chord, final Length position) {
            this.chord = chord;
            this.position = position;
        }

        public Chord chord() {
            return chord;
        }

        public Length position() {
            return position;
        }

        public String notation() {
            return chord.notation();
        }
    }
}
