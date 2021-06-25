package mugres.core.function.builtin.euclides;

import mugres.core.common.Context;
import mugres.core.common.Event;
import mugres.core.common.Length;
import mugres.core.common.Note;
import mugres.core.common.Pitch;
import mugres.core.common.Scale;
import mugres.core.common.Value;
import mugres.core.common.euclides.EuclideanPattern;
import mugres.core.function.Function.EventsFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static mugres.core.common.Note.BASE_OCTAVE;
import static mugres.core.common.Note.C;
import static mugres.core.common.Scale.MINOR_PENTATONIC;
import static mugres.core.common.Value.QUARTER;
import static mugres.core.utils.Randoms.RND;

public class EuclidesPattern extends EventsFunction {
    public EuclidesPattern() {
        super("euclides", "Generates an Euclidean pattern",
                Parameter.of(SIZE, "Total number of steps",
                        Parameter.DataType.INTEGER, false, 0),
                Parameter.of(EVENTS, "Events to play",
                        Parameter.DataType.INTEGER, false, 0),
                Parameter.of(OFFSET, "Pattern offset",
                        Parameter.DataType.INTEGER, true, 0),
                Parameter.of(FIXED_PITCH, "Fixed pitch to play",
                        Parameter.DataType.PITCH, true, null),
                Parameter.of(STARTING_OCTAVE, "Starting octave",
                        Parameter.DataType.INTEGER, true, BASE_OCTAVE),
                Parameter.of(OCTAVES_TO_GENERATE, "Octaves to generate",
                        Parameter.DataType.INTEGER, true, 2),
                Parameter.of(NOTE_VALUE, "Note value",
                        Parameter.DataType.VALUE, true, QUARTER),
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
        final int size = (int)arguments.get(SIZE);
        final int numberOfEvents = (int)arguments.get(EVENTS);
        final int offset = (int)arguments.get(OFFSET);
        final Pitch fixedPitch = (Pitch)arguments.get(FIXED_PITCH);
        final Value noteValue = (Value)arguments.get(NOTE_VALUE);
        final Scale scale = (Scale)arguments.get(SCALE);
        final Note root = (Note)arguments.get(ROOT);
        final int startingOctave = (int)arguments.get(STARTING_OCTAVE);
        final int octavesToGenerate = (int)arguments.get(OCTAVES_TO_GENERATE);
        final List<Pitch> pitches = scale.pitches(root, octavesToGenerate, startingOctave);
        final EuclideanPattern pattern = EuclideanPattern.of(size, numberOfEvents, offset);

        Length actualPosition = Length.ZERO;
        int counter = 0;
        while(actualPosition.getLength() < length.getLength()) {
            if (pattern.eventAt(counter++))
                events.add(Event.of(actualPosition, fixedPitch != null ? fixedPitch : pitches.get(RND.nextInt(pitches.size())),
                        noteValue, 100));
            actualPosition = actualPosition.plus(noteValue);
        }

        return events;
    }

    public static final String SIZE = "size";
    public static final String EVENTS = "events";
    public static final String OFFSET = "offset";
    public static final String FIXED_PITCH = "pitch";
    public static final String STARTING_OCTAVE = "startingOctave";
    public static final String OCTAVES_TO_GENERATE = "octavesToGenerate";
    public static final String NOTE_VALUE = "noteValue";
    public static final String SCALE = "scale";
    public static final String ROOT = "root";
}
