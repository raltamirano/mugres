package mugres.function.builtin.bm;

import mugres.common.*;
import mugres.function.Function;
import mugres.function.builtin.riffer.Riffer;
import mugres.function.common.ByStrategiesFunction;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static mugres.common.Value.QUARTER;
import static mugres.common.Value.SIXTEENTH;
import static mugres.utils.Randoms.*;

public class BlackMetal extends ByStrategiesFunction {
    public BlackMetal() {
        super("blackMetal", "Creates Black Metal riffs");

        addStrategy(new PedalNoteDyads(2));
        addStrategy(new PedalNoteDyads(4));
        addStrategy(new PedalNoteDyads(8));
        addStrategy(new PedalNoteDyads(16));
    }

    static class PedalNoteDyads implements Strategy {
        private final int measures;

        PedalNoteDyads(final int measures) {
            this.measures = measures;
        }

        @Override
        public List<Event> execute(final Context context) {
            if (context.timeSignature().denominator() != QUARTER)
                throw new IllegalArgumentException("Valid Time Signature denominators: 4");

            final List<Event> events = new ArrayList<>();

            final int beatsPerMeasures = context.timeSignature().denominator().denominator();
            final Note root = context.key().root();

            String pattern = random(MEASURE_PATTERNS.get(measures));
            if (pattern != null) {
                final int howManyChords = countChordPlaceholders(pattern);
                final List<String> chordTypes = randoms(random(BM_CHORD_TYPES), howManyChords, true);
                for(int i=1; i<=howManyChords; i++)
                    pattern = pattern.replaceAll("%"+i,  String.format("%s%s ", root.name(), chordTypes.get(i-1)));
            } else {
                final int howManyChords = random(asList(2, 5));
                final List<String> chordTypes = randoms(random(BM_CHORD_TYPES), howManyChords, true);

                int beats = 0;
                for (int index = 0; index < measures; index++) {
                    beats = 0;
                    final int chordsPerMeasure = random(asList(1, 2));
                    while (beats != context.timeSignature().numerator()) {
                        int beatsPerChord = random(asList(2, 4));
                        while (beatsPerChord + beats > beatsPerMeasures)
                            beatsPerChord = random(asList(2, 4));

                        for (int i = 0; i < beatsPerChord; i++)
                            pattern += String.format("%s%s ", root.name(), random(chordTypes));
                        beats += beatsPerChord;
                    }

                    pattern += " |";
                }
            }

            events.addAll(riffer16ths(context, pattern, measures));
            return events;
        }

        private int countChordPlaceholders(final String pattern) {
            final Set<Integer> chords = new HashSet<>();

            final Matcher matcher = Pattern.compile("%(\\d+)").matcher(pattern);
            while(matcher.find()) {
                chords.add(Integer.parseInt(matcher.group(1)));
            }


            return chords.size();
        }

        @Override
        public int measures() {
            return measures;
        }

        private static final Set<Set<String>> BM_CHORD_TYPES = new HashSet<>();
        private static final Map<Integer, Set<String>> MEASURE_PATTERNS = new HashMap<>();

        static {
            // Chord types
            BM_CHORD_TYPES.add(new HashSet<>(asList("5", "b6")));
            BM_CHORD_TYPES.add(new HashSet<>(asList("b3", "4", "b5", "5", "b6")));


            // Measure patterns
            MEASURE_PATTERNS.put(1, new HashSet<>());
            MEASURE_PATTERNS.put(2, new HashSet<>());
            MEASURE_PATTERNS.put(4, new HashSet<>());
            MEASURE_PATTERNS.put(8, new HashSet<>());
            MEASURE_PATTERNS.put(16, new HashSet<>());
            MEASURE_PATTERNS.put(32, new HashSet<>());

            // 4 measures patterns
            MEASURE_PATTERNS.get(4).add("%1 %2 | %3 %2 | %4 %5 | %4 %5");
            MEASURE_PATTERNS.get(4).add("%1 %2 | %1 %2 | %3 %4 | %3 %5");
        }
    }

    /** Creates a 16th note event at desired position */
    private static Event e16th(final Length position, final Pitch pitch) {
        return Event.of(position, pitch, SIXTEENTH, 100);
    }

    private static List<Event> riffer16ths(final Context context, final String riff, final int measures) {
        final Map<String, Object> arguments = new HashMap<>();
        arguments.put(LENGTH_PARAMETER.name(), measures);
        arguments.put("value", SIXTEENTH);
        arguments.put("riff", riff);
        return riffer().execute(context, arguments);
    }

    private static Riffer riffer() {
        return Function.forName("riffer");
    }
}
