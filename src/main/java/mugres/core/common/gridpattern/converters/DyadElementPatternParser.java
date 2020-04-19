package mugres.core.common.gridpattern.converters;

import mugres.core.common.Context;
import mugres.core.common.Interval;
import mugres.core.common.Note;
import mugres.core.common.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DyadElementPatternParser implements ElementPatternParser<DyadElementPatternParser.Dyad> {
    private static final DyadElementPatternParser INSTANCE = new DyadElementPatternParser();

    private DyadElementPatternParser() {}

    public static DyadElementPatternParser getInstance() {
        return INSTANCE;
    }

    private Dyad convert(final String data) {
        final Matcher matcher = DYAD.matcher(data);
        if (!matcher.find())
            throw new IllegalArgumentException("Invalid dyad specification: " + data);

        final Note root = Note.of(matcher.group(1));
        final Interval interval = matcher.group(2) == null ?
                Interval.UNISON :
                Interval.forShortName(matcher.group(2));
        final String octaveString = matcher.group(3) == null ? "" : matcher.group(3).trim();
        final Integer octave = octaveString.isEmpty() ? null :
                Integer.valueOf(octaveString.substring(1, octaveString.length() - 1));
        final String repetitionsString = matcher.group(4) == null ? "" : matcher.group(4).trim();
        final Integer repetitions = repetitionsString.isEmpty() ? null :
                Integer.valueOf(repetitionsString.substring(1, repetitionsString.length() - 1));
        return Dyad.of(root, interval, octave, repetitions);
    }

    @Override
    public ElementPattern<Dyad> parse(final Context context, final Value slotValue, final String line) {
        final List<Dyad> events = new ArrayList<>();

        final String[] measures = line.trim().split("\\|");
        final boolean useMeasuresNotation = line.contains("|");

        if (useMeasuresNotation) {
            final int slotsInOneMeasure = slotValue.denominator();
            for (final String measure : measures) {
                // Extract notated events
                final List<Dyad> foundEvents = new ArrayList<>();
                final Matcher matcher = DYAD_EVENTS.matcher(measure);
                while(matcher.find()) {
                    final String data = matcher.group().trim();
                    if (!data.isEmpty()) {
                        if (NO_EVENT.equals(data))
                            throw new IllegalArgumentException("Malformed riff pattern: You can't mix " +
                                    "measures notation with slots notation");
                        else
                            foundEvents.add(convert(data));
                    }
                }

                // Check we have room for implicit-repeated events in the current measure
                final int explicitSlots = foundEvents.stream()
                        .filter(e -> e.repetitions != null)
                        .map(e -> e.repetitions)
                        .reduce(Integer::sum).orElse(0);
                if (explicitSlots > slotsInOneMeasure)
                    throw new RuntimeException("When using measures notation, explicit repetitions can not " +
                            "be more than the GridPattern's Note Value's denominator");

                // Uniformly divide all the implicit free slots in the current measure
                // among the remaining events
                final int remainingSlots = slotsInOneMeasure - explicitSlots;
                final List<Dyad> nonExplicitEvents = foundEvents.stream()
                        .filter(e -> e.repetitions == null).collect(Collectors.toList());

                if (nonExplicitEvents.size() > 0)
                    if ((remainingSlots % nonExplicitEvents.size()) != 0)
                        throw new RuntimeException("Remaining slots not a multiple of non-explicit events!");

                final int repetitionsPerEvent = nonExplicitEvents.size() == 0 ?
                        remainingSlots :
                        remainingSlots / nonExplicitEvents.size();
                nonExplicitEvents.forEach(e -> e.repetitions = repetitionsPerEvent);

                // Generate the actual events
                foundEvents.forEach(dyad -> {
                    final int repetitions = dyad.repetitions == null ? 1 : dyad.repetitions;
                    for (int i = 0; i < repetitions; i++)
                        events.add(dyad);
                });
            }
        } else {
            final Matcher matcher = DYAD_EVENTS.matcher(line);
            while (matcher.find()) {
                final String data = matcher.group().trim();
                if (!data.isEmpty()) {
                    if (NO_EVENT.equals(data)) {
                        events.add(null);
                    } else {
                        final Dyad dyad = convert(data);
                        final int repetitions = dyad.repetitions == null ? 1 : dyad.repetitions;
                        for (int i = 0; i < repetitions; i++)
                            events.add(dyad);
                    }
                }
            }
        }

        return ElementPattern.of("XXX", events);
    }

    public static class Dyad {
        private final Note root;
        private final Note next;
        private final Interval interval;
        private final Integer octave;
        private Integer repetitions;

        private Dyad(final Note root, final Interval interval, final Integer octave, final Integer repetitions) {
            this.root = root;
            this.interval = interval;
            this.next = root.up(interval);
            this.octave = octave;
            this.repetitions = repetitions;
        }

        public static Dyad of(final Note root, final Interval interval) {
            return of(root, interval, null);
        }

        public static Dyad of(final Note root, final Interval interval,
                              final Integer octave) {
            return new Dyad(root, interval, octave, null);
        }

        public static Dyad of(final Note root, final Interval interval,
                              final Integer octave, final Integer repetitions) {
            return new Dyad(root, interval, octave, repetitions);
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

    private static final Pattern DYAD = Pattern.compile("((?:C|D|E|F|G|A|B)#?)(b2|2|b3|3|4|#4|b5|5|b6|6|bb7|b7|7|8)?(\\[-?\\d\\])?(\\{[1-9][0-9]*\\})?");
    private static final Pattern DYAD_EVENTS = Pattern.compile("(" + DYAD.pattern() + "|\\s+|-)");
}