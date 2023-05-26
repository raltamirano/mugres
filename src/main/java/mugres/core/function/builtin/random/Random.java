package mugres.core.function.builtin.random;

import mugres.core.common.Context;
import mugres.core.common.DataType;
import mugres.core.common.Event;
import mugres.core.common.Length;
import mugres.core.common.Note;
import mugres.core.common.Pitch;
import mugres.core.common.Scale;
import mugres.core.common.Value;
import mugres.core.function.Function.EventsFunction;
import mugres.core.parametrizable.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static mugres.core.common.Note.BASE_OCTAVE;
import static mugres.core.common.Note.C;
import static mugres.core.common.Scale.MINOR_PENTATONIC;
import static mugres.core.common.Value.QUARTER;
import static mugres.core.utils.Randoms.RND;

public class Random extends EventsFunction {
    public Random() {
        super("random", "Generates random pitches",
                Parameter.of(STARTING_OCTAVE, "Starting octave",
                        DataType.INTEGER, true, BASE_OCTAVE),
                Parameter.of(OCTAVES_TO_GENERATE, "Octaves to generate",
                        DataType.INTEGER, true, 2),
                Parameter.of(NOTE_VALUE, "Note value",
                        DataType.VALUE, true, QUARTER),
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
        final Value noteValue = (Value)arguments.get(NOTE_VALUE);
        final Scale scale = (Scale)arguments.get(SCALE);
        final Note root = (Note)arguments.get(ROOT);
        final int startingOctave = (int)arguments.get(STARTING_OCTAVE);
        final int octavesToGenerate = (int)arguments.get(OCTAVES_TO_GENERATE);
        final List<Pitch> pitches = scale.pitches(root, octavesToGenerate, startingOctave);

        Length actualPosition = Length.ZERO;
        while(actualPosition.length() < length.length()) {
            events.add(Event.of(actualPosition, pitches.get(RND.nextInt(pitches.size())), noteValue, 100));
            actualPosition = actualPosition.plus(noteValue);
        }

        return events;
    }

    public static final String STARTING_OCTAVE = "startingOctave";
    public static final String OCTAVES_TO_GENERATE = "octavesToGenerate";
    public static final String NOTE_VALUE = "noteValue";
    public static final String SCALE = "scale";
    public static final String ROOT = "root";
}
