package mugres.core.function.builtin.chords;

import mugres.core.common.Context;
import mugres.core.common.Event;
import mugres.core.common.Length;
import mugres.core.common.Value;
import mugres.core.common.chords.Chord;
import mugres.core.common.gridpattern.GridEvent;
import mugres.core.common.gridpattern.GridPattern;
import mugres.core.common.gridpattern.converters.ChordElementPatternParser;
import mugres.core.function.Function.EventsFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static mugres.core.common.Value.WHOLE;

public class Chords extends EventsFunction {
    public Chords() {
        super("chords", "Reproduces a predefined chord progression",
                Parameter.of("progression", "The chord progression to play", Parameter.DataType.TEXT),
                Parameter.of("octave", "Base octave", Parameter.DataType.INTEGER,
                        true, 2),
                Parameter.of("value", "Note value for events", Parameter.DataType.VALUE,
                        true, WHOLE));
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final Length length = lengthFromNumberOfMeasures(context, arguments);
        final String progression = (String)arguments.get("progression");
        final int baseOctave = (int)arguments.get("octave");
        final Value value = (Value)arguments.get("value");

        final GridPattern<ChordElementPatternParser.ChordEvent> chordProgression =
                GridPattern.parse(progression, ChordElementPatternParser.getInstance(), context, value);

        if (!length.equals(chordProgression.getLength()))
            throw new RuntimeException("Progression's length does not match function call's length!");

        final List<Event> events = new ArrayList<>();

        ChordElementPatternParser.ChordEvent lastChordEvent = null;
        for(GridEvent<ChordElementPatternParser.ChordEvent> e : chordProgression.getEvents()) {
            final Length position = chordProgression.getDivision().length().multiply(e.getSlot() - 1);

            ChordElementPatternParser.ChordEvent chordEvent = e.getData();
            if (chordEvent != null) {
                lastChordEvent = chordEvent;
            } else {
                if (chordProgression.isKeepPlaying())
                    chordEvent = lastChordEvent;
            }

            if (chordEvent != null) {
                final int octave = chordEvent.getOctave() != null ? chordEvent.getOctave() : baseOctave;
                final int velocity = 100;

                Chord.of(chordEvent.getRoot(), chordEvent.getType()).pitches(octave)
                        .forEach(p -> events.add(Event.of(position, p, chordProgression.getDivision(), velocity)));
            }
        }

        return events;
    }
}
