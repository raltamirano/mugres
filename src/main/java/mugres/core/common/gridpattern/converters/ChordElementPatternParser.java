package mugres.core.common.gridpattern.converters;

import mugres.core.common.Context;
import mugres.core.common.Note;
import mugres.core.common.Value;
import mugres.core.common.chords.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChordElementPatternParser implements ElementPatternParser<ChordElementPatternParser.ChordEvent> {
    private static final ChordElementPatternParser INSTANCE = new ChordElementPatternParser();

    private ChordElementPatternParser() {}

    public static ChordElementPatternParser getInstance() {
        return INSTANCE;
    }

    private ChordEvent convert(final String data) {
        final Matcher matcher = CHORD.matcher(data);
        if (!matcher.find())
            throw new IllegalArgumentException("Invalid chord specification: " + data);

        final Note root = Note.of(matcher.group(1));
        final Type type = matcher.group(2) == null ?
                Type.MAJOR :
                Type.forAbbreviation(matcher.group(2));
        final String octaveString = matcher.group(3) == null ? "" : matcher.group(3).trim();
        final Integer octave = octaveString.isEmpty() ? null :
                Integer.valueOf(octaveString.substring(1, octaveString.length() - 1));
        final String repetitionsString = matcher.group(4) == null ? "" : matcher.group(4).trim();
        final Integer repetitions = repetitionsString.isEmpty() ? null :
                Integer.valueOf(repetitionsString.substring(1, repetitionsString.length() - 1));
        return ChordEvent.of(root, type, octave, repetitions);
    }

    @Override
    public ElementPattern<ChordEvent> parse(final Context context, final Value slotValue, final String line) {
        final List<ChordEvent> events = new ArrayList<>();

        final String[] measures = line.trim().split("\\|");
        final boolean useMeasuresNotation = line.contains("|");

        if (useMeasuresNotation) {
            final int slotsInOneMeasure = slotValue.denominator();
            for (final String measure : measures) {
                // Extract notated events
                final List<ChordEvent> foundEvents = new ArrayList<>();
                final Matcher matcher = CHORD_EVENTS.matcher(measure);
                while(matcher.find()) {
                    final String data = matcher.group().trim();
                    if (!data.isEmpty()) {
                        if (NO_EVENT.equals(data))
                            throw new IllegalArgumentException("Malformed chord progression: You can't mix " +
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
                final List<ChordEvent> nonExplicitEvents = foundEvents.stream()
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
            final Matcher matcher = CHORD_EVENTS.matcher(line);
            while (matcher.find()) {
                final String data = matcher.group().trim();
                if (!data.isEmpty()) {
                    if (NO_EVENT.equals(data)) {
                        events.add(null);
                    } else {
                        final ChordEvent chordEvent = convert(data);
                        final int repetitions = chordEvent.repetitions == null ? 1 : chordEvent.repetitions;
                        for (int i = 0; i < repetitions; i++)
                            events.add(chordEvent);
                    }
                }
            }
        }

        return ElementPattern.of("XXX", events);
    }

    public static class ChordEvent {
        private final Note root;
        private final Type type;
        private final Integer octave;
        private Integer repetitions;

        private ChordEvent(final Note root, final Type type, final Integer octave,
                           final Integer repetitions) {
            this.root = root;
            this.type = type;
            this.octave = octave;
            this.repetitions = repetitions;
        }


        public static ChordEvent of(final Note root, final Type type, final Integer octave,
                                    final Integer repetitions) {
            return new ChordEvent(root, type, octave, repetitions);
        }

        public Note getRoot() {
            return root;
        }

        public Type getType() {
            return type;
        }

        public Integer getOctave() {
            return octave;
        }

        @Override
        public String toString() {
            return String.format("%s%s", root, type.notation());
        }
    }

    private static final Pattern CHORD = Pattern.compile("((?:C|D|E|F|G|A|B)#?)(.*)?\\s+(\\[-?\\d\\])?(\\{[1-9][0-9]*\\})?");
    private static final Pattern CHORD_EVENTS = Pattern.compile("(" + CHORD.pattern() + "|\\s+|-)");
}