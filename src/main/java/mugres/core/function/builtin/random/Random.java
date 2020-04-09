package mugres.core.function.builtin.random;

import mugres.core.common.*;
import mugres.core.function.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static mugres.core.common.Value.QUARTER;
import static mugres.core.function.Function.Parameter.DataType.INTEGER;
import static mugres.core.function.Function.Parameter.DataType.VALUE;

public class Random extends Function {
    public Random() {
        super("Random", "Generates random pitches",
                new Parameter("startingOctave", "Starting octave",
                        INTEGER, true, 3),
                new Parameter("octavesToGenerate", "Octaves to generate",
                        INTEGER, true, 2),
                new Parameter("noteValue", "Note value",
                        VALUE, true, QUARTER)
        );
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final List<Event> events = new ArrayList<>();
        final Length length = readMeasuresLength(context, arguments);
        final Value noteValue = (Value)arguments.get("noteValue");
        final int startingOctave = (int)arguments.get("startingOctave");
        final int octavesToGenerate = (int)arguments.get("octavesToGenerate");
        final List<Pitch> pitches = Scale.MAJOR_PENTATONIC.pitches(Note.C, octavesToGenerate, startingOctave);

        Length actualLength = Length.ZERO;
        while(actualLength.getLength() < length.getLength()) {
            events.add(Event.of(actualLength, pitches.get(RND.nextInt(pitches.size())), noteValue, 100));
            actualLength = actualLength.plus(noteValue.length());
        }

        return events;
    }

    private static final java.util.Random RND = new java.util.Random();
}
