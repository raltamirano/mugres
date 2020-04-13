package mugres.core.function.builtin.riffer;

import mugres.core.common.*;
import mugres.core.common.gridpattern.GridEvent;
import mugres.core.common.gridpattern.GridPattern;
import mugres.core.common.gridpattern.converters.DyadElementPatternParser;
import mugres.core.function.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static mugres.core.common.Interval.UNISON;
import static mugres.core.common.Value.QUARTER;

public class Riffer extends Function {
    public Riffer() {
        super("riffer", "Reproduces a predefined riff",
                Parameter.of("riff", "The riff to play", Parameter.DataType.TEXT),
                Parameter.of("octave", "Base octave", Parameter.DataType.INTEGER,
                        true, 3),
                Parameter.of("value", "Note value for events", Parameter.DataType.VALUE,
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
