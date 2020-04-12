package mugres.core.common.gridpattern.converters;

import mugres.core.common.Interval;
import mugres.core.common.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mugres.core.common.gridpattern.GridPattern.NO_EVENT;

public class DyadDataConverter implements DataConverter<DyadDataConverter.Dyad> {
    private static final DyadDataConverter INSTANCE = new DyadDataConverter();

    private DyadDataConverter() {}

    public static DyadDataConverter getInstance() {
        return INSTANCE;
    }

    @Override
    public Dyad convert(final String data) {
        final Matcher matcher = DYAD.matcher(data);
        if (!matcher.find())
            throw new IllegalArgumentException("Invalid dyad specification: " + data);

        final Note root = Note.of(matcher.group(1));
        final Interval interval = Interval.forShortName(matcher.group(2));
        final String octaveString = matcher.group(3);
        final Integer octave = octaveString.trim().isEmpty() ?
                null :
                Integer.valueOf(octaveString.substring(1, octaveString.length() - 1));
        return Dyad.of(root, interval, octave);
    }

    @Override
    public List<Dyad> tokenize(final String line) {
        final List<Dyad> events = new ArrayList<>();

        final Matcher matcher = DYAD_EVENTS.matcher(line);
        while(matcher.find()) {
            final String data = matcher.group().trim();
            if (!data.isEmpty()) {
                if (NO_EVENT.equals(data))
                    events.add(null);
                else
                    events.add(convert(data));
            }
        }

        return events;
    }

    public static class Dyad {
        private final Note root;
        private final Note next;
        private final Interval interval;
        private final Integer octave;

        private Dyad(final Note root, final Interval interval, final Integer octave) {
            this.root = root;
            this.interval = interval;
            this.next = root.up(interval);
            this.octave = octave;
        }

        public static Dyad of(final Note root, final Interval interval) {
            return of(root, interval, null);
        }

        public static Dyad of(final Note root, final Interval interval, final Integer octave) {
            return new Dyad(root, interval, octave);
        }

        public Note getRoot() {
            return root;
        }

        public Note getNext() {
            return next;
        }

        public Interval getInterval() {
            return interval;
        }

        public Integer getOctave() {
            return octave;
        }

        @Override
        public String toString() {
            return String.format("Root: %s - Next: %s (%s)", root, next, interval.shortName());
        }
    }

    private static final Pattern DYAD = Pattern.compile("(C|C#|D|D#|E|F|F#|G|G#|A|A#|B)(b2|2|b3|3|4|#4|b5|5|b6|6|bb7|b7|7|8)(\\[-?\\d\\])?");
    private static final Pattern DYAD_EVENTS = Pattern.compile("(" + DYAD.pattern() + "|\\s+|-)");
}