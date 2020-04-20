package mugres.core.function.builtin.random;

import mugres.core.common.*;
import mugres.core.function.Function.EventsFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static mugres.core.common.Value.QUARTER;
import static mugres.core.function.Function.Parameter.DataType.INTEGER;
import static mugres.core.function.Function.Parameter.DataType.VALUE;

public class Random extends EventsFunction {
    public Random() {
        super("random", "Generates random pitches",
                Parameter.of("startingOctave", "Starting octave",
                        INTEGER, true, 3),
                Parameter.of("octavesToGenerate", "Octaves to generate",
                        INTEGER, true, 2),
                Parameter.of("noteValue", "Note value",
                        VALUE, true, QUARTER)
        );
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final List<Event> events = new ArrayList<>();
        final Length length = lengthFromNumberOfMeasures(context, arguments);
        final Value noteValue = (Value)arguments.get("noteValue");
        final int startingOctave = (int)arguments.get("startingOctave");
        final int octavesToGenerate = (int)arguments.get("octavesToGenerate");
        final List<Pitch> pitches = Scale.MAJOR_PENTATONIC.pitches(Note.C, octavesToGenerate, startingOctave);

        Length actualPosition = Length.ZERO;
        while(actualPosition.getLength() < length.getLength()) {
            events.add(Event.of(actualPosition, pitches.get(RND.nextInt(pitches.size())), noteValue, 100));
            actualPosition = actualPosition.plus(noteValue);
        }

        return events;
    }

    private static final java.util.Random RND = new java.util.Random();
}
