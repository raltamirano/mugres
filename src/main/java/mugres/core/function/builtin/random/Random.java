package mugres.core.function.builtin.random;

import mugres.core.common.*;
import mugres.core.function.Function.EventsFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static mugres.core.common.Note.BASE_OCTAVE;
import static mugres.core.common.Note.C;
import static mugres.core.common.Scale.MINOR_PENTATONIC;
import static mugres.core.common.Value.QUARTER;
import static mugres.core.function.Function.Parameter.DataType.*;
import static mugres.core.utils.Randoms.RND;

public class Random extends EventsFunction {
    public Random() {
        super("random", "Generates random pitches",
                Parameter.of("startingOctave", "Starting octave",
                        INTEGER, true, BASE_OCTAVE),
                Parameter.of("octavesToGenerate", "Octaves to generate",
                        INTEGER, true, 2),
                Parameter.of("noteValue", "Note value",
                        VALUE, true, QUARTER),
                Parameter.of("scale", "Scale to pick notes from",
                        SCALE, true, MINOR_PENTATONIC),
                Parameter.of("root", "Scale root",
                        NOTE, true, C)
        );
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final List<Event> events = new ArrayList<>();
        final Length length = lengthFromNumberOfMeasures(context, arguments);
        final Value noteValue = (Value)arguments.get("noteValue");
        final Scale scale = (Scale)arguments.get("scale");
        final Note root = (Note)arguments.get("root");
        final int startingOctave = (int)arguments.get("startingOctave");
        final int octavesToGenerate = (int)arguments.get("octavesToGenerate");
        final List<Pitch> pitches = scale.pitches(root, octavesToGenerate, startingOctave);

        Length actualPosition = Length.ZERO;
        while(actualPosition.getLength() < length.getLength()) {
            events.add(Event.of(actualPosition, pitches.get(RND.nextInt(pitches.size())), noteValue, 100));
            actualPosition = actualPosition.plus(noteValue);
        }

        return events;
    }
}
