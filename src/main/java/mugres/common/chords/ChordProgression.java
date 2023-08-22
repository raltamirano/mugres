package mugres.common.chords;

import mugres.common.Context;
import mugres.common.Length;
import mugres.common.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ChordProgression {
    private final Context context;
    private final int measures;
    private final Length length;
    private final Map<Length, Chord> events = new TreeMap<>();

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

    public ChordProgression event(final int measure, final Chord chord) {
        return event(measure, 1, chord);
    }

    public ChordProgression event(final int measure, final int beat, final Chord chord) {
        return event(Position.of(measure, beat).asLength(context), chord);
    }

    public ChordProgression event(final Length at, final Chord chord) {
        if (at.greaterThan(length))
            throw new IllegalArgumentException("Chord event position exceeds chord progression length!");
        if (chord == null)
            throw new IllegalArgumentException("chord");

        events.put(at, chord);
        return this;
    }

    public Chord chordAt(final Length position) {
        if (position.greaterThan(length))
            throw new IllegalArgumentException("Position outside of chord progression!");

        if (events.isEmpty())
            return null;

        for(Length l : events.keySet())
            if (position.greaterThanOrEqual(l))
                return events.get(l);

        throw new RuntimeException("Internal error getting chord from chord progression");
    }

    public int measures() {
        return measures;
    }

    public Length length() {
        return length;
    }

    public List<ChordEvent> events() {
        return calculateEvents();
    }

    private List<ChordEvent> calculateEvents() {
        final List<ChordEvent> result = new ArrayList<>();
        final List<Length> keys = events.keySet().stream().sorted().collect(Collectors.toList());

        for (int i=0; i<keys.size(); i++) {
            final Length eventLength;
            if (i == (keys.size() - 1))
                eventLength = length.minus(keys.get(i));
            else
                eventLength = keys.get(i+1).minus(keys.get(i));
            result.add(new ChordEvent(events.get(keys.get(i)), keys.get(i), eventLength));
        }

        return result;
    }

    public static class ChordEvent {
        private final Chord chord;
        private final Length position;
        private final Length length;

        private ChordEvent(final Chord chord, final Length position, final Length length) {
            this.chord = chord;
            this.position = position;
            this.length = length;
        }

        public Chord chord() {
            return chord;
        }

        public Length position() {
            return position;
        }
        public Length length() {
            return length;
        }

        public String notation() {
            return chord.notation();
        }

        @Override
        public String toString() {
            return "ChordEvent{" +
                    "chord=" + notation() +
                    ", position=" + position +
                    ", length=" + length +
                    '}';
        }
    }
}
