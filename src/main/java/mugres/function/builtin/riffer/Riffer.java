package mugres.function.builtin.riffer;

import mugres.common.*;
import mugres.common.gridpattern.GridEvent;
import mugres.common.gridpattern.GridPattern;
import mugres.common.gridpattern.converters.DyadElementPatternParser;
import mugres.function.Function.EventsFunction;
import mugres.parametrizable.Parameter;
import mugres.tracker.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static mugres.common.Interval.UNISON;
import static mugres.common.Value.QUARTER;

public class Riffer extends EventsFunction {
    public Riffer() {
        super("riffer", "Reproduces a predefined riff",
                Parameter.of("riff", "Riff", 1, "The riff to play", DataType.TEXT, false),
                Parameter.of("octave", "Octave", 2, "Base octave", DataType.INTEGER,
                        true, 3),
                Parameter.of("value", "Value", 3, "Note value for events", DataType.VALUE,
                        true, QUARTER));
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final Length length = lengthFromNumberOfMeasures(context, arguments);
        final String riff = (String)arguments.get("riff");
        final int baseOctave = (int)arguments.get("octave");
        final Value value = (Value)arguments.get("value");

        final GridPattern<DyadElementPatternParser.Dyad> riffPattern =
                GridPattern.parse(riff, DyadElementPatternParser.getInstance(), context, value);

        if (!length.equals(riffPattern.getLength()))
            throw new RuntimeException("Riff's length does not match function call's length!");

        final List<Event> events = new ArrayList<>();

        DyadElementPatternParser.Dyad lastDyad = null;
        for(GridEvent<DyadElementPatternParser.Dyad> dyadEvent : riffPattern.getEvents()) {
            final Length position = riffPattern.getDivision().length().multiply(dyadEvent.getSlot() - 1);

            DyadElementPatternParser.Dyad dyad = dyadEvent.getData();
            if (dyad != null) {
                lastDyad = dyad;
            } else {
                if (riffPattern.isKeepPlaying())
                    dyad = lastDyad;
            }

            if (dyad != null) {
                final int octave = dyad.getOctave() != null ? dyad.getOctave() : baseOctave;
                final Pitch root = dyad.getRoot().pitch(octave);
                final Pitch next = dyad.getInterval() != UNISON ?
                        dyad.getRoot().pitch(octave).up(dyad.getInterval()) : root;
                final int velocity = 100;

                events.add(Event.of(position, root, riffPattern.getDivision(), velocity));
                if (dyad.getInterval() != UNISON)
                    events.add(Event.of(position, next, riffPattern.getDivision(), velocity));
            }
        }

        return events;
    }
}
