package mugres.function.builtin.text;

import mugres.common.Context;
import mugres.common.DataType;
import mugres.tracker.Event;
import mugres.common.Length;
import mugres.common.Note;
import mugres.common.Pitch;
import mugres.common.Scale;
import mugres.common.Value;
import mugres.function.Function.EventsFunction;
import mugres.parametrizable.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static mugres.common.Note.BASE_OCTAVE;
import static mugres.common.Note.C;
import static mugres.common.Scale.MINOR_PENTATONIC;
import static mugres.common.Value.EIGHTH;
import static mugres.common.Value.HALF;
import static mugres.common.Value.QUARTER;
import static mugres.common.Value.SIXTEENTH;
import static mugres.common.Value.WHOLE;
import static mugres.utils.Randoms.randomBetween;

public class TextMelody extends EventsFunction {
    public TextMelody() {
        super("textMelody", "Translates a text into a melody",
                Parameter.of(SOURCE_TEXT, "Text", 1, "Source text",
                        DataType.TEXT, false, ""),
                Parameter.of(STARTING_OCTAVE, "Starting octave", 2, "Starting octave",
                        DataType.INTEGER, true, BASE_OCTAVE),
                Parameter.of(OCTAVES_TO_GENERATE, "Octaves to generate", 3, "Octaves to generate",
                        DataType.INTEGER, true, 2),
                Parameter.of(SCALE, "Scale", 4, "Scale to pick notes from",
                        DataType.SCALE, true, MINOR_PENTATONIC),
                Parameter.of(ROOT, "Root", 5, "Scale root",
                        DataType.NOTE, true, C)
        );
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final List<Event> events = new ArrayList<>();
        final Length length = lengthFromNumberOfMeasures(context, arguments);
        final String sourceText = (String)arguments.get(SOURCE_TEXT);
        final Scale scale = (Scale)arguments.get(SCALE);
        final Note root = (Note)arguments.get(ROOT);
        final int startingOctave = (int)arguments.get(STARTING_OCTAVE);
        final int octavesToGenerate = (int)arguments.get(OCTAVES_TO_GENERATE);
        final List<Pitch> pitches = scale.pitches(root, octavesToGenerate, startingOctave);
        final List<Value> values = asList(WHOLE, HALF, QUARTER, EIGHTH, SIXTEENTH);

        Length actualPosition = Length.ZERO;
        int index = 0;
        while(actualPosition.length() < length.length()) {
            final char next = sourceText.charAt(index++ % sourceText.length());

            final Value noteValue;
            final int velocity;
            final boolean rest;
            switch(next) {
                case ' ':
                case '-':
                    noteValue = Value.EIGHTH;
                    velocity = 0;
                    rest = true;
                    break;
                case '.':
                    noteValue = QUARTER;
                    velocity = 0;
                    rest = true;
                    break;
                case '\n':
                    noteValue = HALF;
                    velocity = 0;
                    rest = true;
                    break;
                default:
                    noteValue = values.get(((int)next) % values.size());
                    velocity = Character.isUpperCase(next) ? randomBetween(100, 110) : randomBetween(80, 99);
                    rest = false;
            }

            Value actualNoteValue = null;
            if (actualPosition.plus(noteValue).greaterThan(length)) {
                boolean alternativeSet = false;
                for(Value v : Value.values()) {
                    if (actualPosition.plus(v).lessThanOrEqual(length)) {
                        actualNoteValue = v;
                        alternativeSet = true;
                        break;
                    }
                }
                if (!alternativeSet)
                    throw new RuntimeException("Error checking length!");
            } else {
                actualNoteValue = noteValue;
            }

            if (!rest) {
                final Pitch pitch = pitches.get(((int)next) % pitches.size());
                events.add(Event.of(actualPosition, pitch, actualNoteValue, velocity));
            }
            actualPosition = actualPosition.plus(actualNoteValue);
        }

        return events;
    }

    public static final String SOURCE_TEXT = "sourceText";
    public static final String STARTING_OCTAVE = "startingOctave";
    public static final String OCTAVES_TO_GENERATE = "octavesToGenerate";
    public static final String SCALE = "scale";
    public static final String ROOT = "root";
}
