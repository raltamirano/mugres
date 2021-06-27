package mugres.core.function.builtin.euclides;

import mugres.core.common.Context;
import mugres.core.common.Event;
import mugres.core.common.Length;
import mugres.core.common.Note;
import mugres.core.common.Pitch;
import mugres.core.common.Scale;
import mugres.core.common.euclides.EuclideanPattern;
import mugres.core.function.Function.EventsFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static mugres.core.common.Note.BASE_OCTAVE;
import static mugres.core.common.Note.C;
import static mugres.core.common.Scale.MINOR_PENTATONIC;
import static mugres.core.utils.Randoms.RND;

public class Euclides extends EventsFunction {
    public Euclides() {
        super("euclides", "Plays an Euclidean polyrhythm",
                Parameter.of(PATTERNS, "Patterns",
                        Parameter.DataType.INTEGER, false, emptyList(), true),
                Parameter.of(PITCHES, "Pitches to play",
                        Parameter.DataType.PITCH, true, null, true),
                Parameter.of(STARTING_OCTAVE, "Starting octave",
                        Parameter.DataType.INTEGER, true, BASE_OCTAVE),
                Parameter.of(OCTAVES_TO_GENERATE, "Octaves to generate",
                        Parameter.DataType.INTEGER, true, 2),
                Parameter.of(SCALE, "Scale to pick notes from",
                        Parameter.DataType.SCALE, true, MINOR_PENTATONIC),
                Parameter.of(ROOT, "Scale root",
                        Parameter.DataType.NOTE, true, C)
        );
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final List<Event> events = new ArrayList<>();
        final Length length = lengthFromNumberOfMeasures(context, arguments);
        final List<Integer> patterns = (List<Integer>)arguments.get(PATTERNS);
        final List<Pitch> fixedPitches = (List<Pitch>)arguments.get(PITCHES);
        final Scale scale = (Scale)arguments.get(SCALE);
        final Note root = (Note)arguments.get(ROOT);
        final int startingOctave = (int)arguments.get(STARTING_OCTAVE);
        final int octavesToGenerate = (int)arguments.get(OCTAVES_TO_GENERATE);
        final List<Pitch> pitches = scale.pitches(root, octavesToGenerate, startingOctave);

        int patternIndex = 0;
        for(int p : patterns) {
            final EuclideanPattern pattern = EuclideanPattern.of(PATTERN_RESOLUTION, p, 0);
            final Length stepSize = context.timeSignature().measureLength().divide(pattern.steps());
            Length actualPosition = Length.ZERO;
            int counter = 0;
            while (actualPosition.length() < length.length()) {
                if (pattern.eventAt(counter++)) {
                    final Pitch pitch = fixedPitches != null ? fixedPitches.get(patternIndex % fixedPitches.size()) : pitches.get(RND.nextInt(pitches.size()));
                    events.add(Event.of(actualPosition, pitch, stepSize, 100));
                }
                actualPosition = actualPosition.plus(stepSize);
            }
            patternIndex++;
        }

        return events;
    }

    public static final String PATTERNS = "patterns";
    public static final String PITCHES = "pitches";
    public static final String STARTING_OCTAVE = "startingOctave";
    public static final String OCTAVES_TO_GENERATE = "octavesToGenerate";
    public static final String SCALE = "scale";
    public static final String ROOT = "root";

    private static final int PATTERN_RESOLUTION = 128;
}
