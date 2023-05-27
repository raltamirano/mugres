package mugres.function.builtin.euclides;

import mugres.common.Context;
import mugres.common.DataType;
import mugres.common.Event;
import mugres.common.Length;
import mugres.common.Note;
import mugres.common.Pitch;
import mugres.common.Scale;
import mugres.common.euclides.EuclideanPattern;
import mugres.function.Function.EventsFunction;
import mugres.parametrizable.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static mugres.common.Note.BASE_OCTAVE;
import static mugres.common.Note.C;
import static mugres.common.Scale.MINOR_PENTATONIC;
import static mugres.utils.Randoms.RND;

public class Euclides extends EventsFunction {
    public Euclides() {
        super("euclides", "Plays an Euclidean polyrhythm",
                Parameter.of(PATTERNS, "Patterns",
                        DataType.EUCLIDEAN_PATTERN, false, emptyList(), true),
                Parameter.of(PITCHES, "Pitches for every pattern (same order)",
                        DataType.PITCH, true, null, true),
                Parameter.of(CYCLE, "Cycle length (defaults to one measure)",
                        DataType.LENGTH, true, null),
                Parameter.of(STARTING_OCTAVE, "Starting octave",
                        DataType.INTEGER, true, BASE_OCTAVE),
                Parameter.of(OCTAVES_TO_GENERATE, "Octaves to generate",
                        DataType.INTEGER, true, 2),
                Parameter.of(SCALE, "Scale to pick notes from",
                        DataType.SCALE, true, MINOR_PENTATONIC),
                Parameter.of(ROOT, "Scale root",
                        DataType.NOTE, true, C)
        );
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final List<Event> events = new ArrayList<>();
        final Length length = lengthFromNumberOfMeasures(context, arguments);
        final List<EuclideanPattern> patterns = (List<EuclideanPattern>)arguments.get(PATTERNS);
        final List<Pitch> fixedPitches = (List<Pitch>)arguments.get(PITCHES);
        final Length cycleLength = getCycleLength(context, arguments);
        final Scale scale = (Scale)arguments.get(SCALE);
        final Note root = (Note)arguments.get(ROOT);
        final int startingOctave = (int)arguments.get(STARTING_OCTAVE);
        final int octavesToGenerate = (int)arguments.get(OCTAVES_TO_GENERATE);
        final List<Pitch> pitches = scale.pitches(root, octavesToGenerate, startingOctave);

        if (fixedPitches != null && fixedPitches.size() != patterns.size())
            throw new IllegalArgumentException("When provided, number of fixed pitches must match the number patterns");

        int patternIndex = 0;
        for(final EuclideanPattern pattern : patterns) {
            final Length stepSize = cycleLength.divide(pattern.steps());
            Length actualPosition = Length.ZERO;
            int counter = 0;
            int eventCounter = 0;
            while (actualPosition.length() < length.length()) {
                if (pattern.eventAt(counter++)) {
                    final Pitch pitch = fixedPitches != null ?
                            fixedPitches.get(patternIndex % fixedPitches.size()) :
                            pitches.get(RND.nextInt(pitches.size()));
                    events.add(Event.of(actualPosition, pitch, stepSize, eventCounter++ % pattern.events() == 0 ? HARD : SOFT));
                }
                actualPosition = actualPosition.plus(stepSize);
            }
            patternIndex++;
        }

        return events;
    }

    private Length getCycleLength(final Context context, final Map<String, Object> arguments) {

        try {
            final Length length = (Length) arguments.get(CYCLE);
            return length != null ?
                    length :
                    context.timeSignature().measureLength();
        } catch(final Throwable ignore) {
            return context.timeSignature().measureLength();
        }
    }

    public static final String PATTERNS = "patterns";
    public static final String PITCHES = "pitches";
    public static final String CYCLE = "cycle";
    public static final String STARTING_OCTAVE = "startingOctave";
    public static final String OCTAVES_TO_GENERATE = "octavesToGenerate";
    public static final String SCALE = "scale";
    public static final String ROOT = "root";

    private static final int HARD = 110;
    private static final int SOFT = 100;
}
