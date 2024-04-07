package mugres.common.chords;

import mugres.common.Context;
import mugres.common.Length;
import mugres.common.Note;
import mugres.common.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    public static ChordProgression of(final Context context, final String input) {
        final Matcher matcher = PROGRESSION.matcher(input);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid chord progression: " + input);

        final int measures = Integer.parseInt(matcher.group("measures"));
        final ChordProgression chordProgression = of(context, measures);

        final Matcher eventsMatcher = EVENT.matcher(matcher.group("events"));
        while(eventsMatcher.find()) {
            final int measure = Integer.parseInt(eventsMatcher.group("measure"));
            final int beat = Integer.parseInt(eventsMatcher.group("beat"));
            final Note root = Note.of(eventsMatcher.group("root"));
            final Type type = eventsMatcher.group("type") == null ?
                    Type.MAJOR :
                    Type.forAbbreviation(eventsMatcher.group("type"));
            final Chord chord = Chord.of(root, type);
    
            chordProgression.event(measure, beat, chord);
        }

        return chordProgression;
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

    private static final Pattern EVENT = Pattern.compile("(?<measure>\\d+):(?<beat>\\d+):(?<chord>(?<root>(C|D|E|F|G|A|B)#?)(?<type>.*?(?=\\*))?)\\*");
    private static final Pattern PROGRESSION = Pattern.compile("^(?<measures>\\d+)>(?<events>(" + EVENT.pattern() + ")+)$");

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
