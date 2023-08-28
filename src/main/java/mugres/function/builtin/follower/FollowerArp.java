package mugres.function.builtin.follower;

import mugres.common.Context;
import mugres.common.Length;
import mugres.common.Pitch;
import mugres.common.chords.ChordProgression.ChordEvent;
import mugres.function.utils.Arpeggios;
import mugres.function.Function.EventsFunction;
import mugres.function.utils.EventAccumulator;
import mugres.parametrizable.Parameter;
import mugres.tracker.Event;

import java.util.List;
import java.util.Map;

import static mugres.common.DataType.INTEGER;
import static mugres.common.DataType.TEXT;
import static mugres.common.Pitch.BASE_OCTAVE;
import static mugres.function.utils.EventAccumulator.OnExcessAction.SHORTEN;

public class FollowerArp extends EventsFunction {
    public FollowerArp() {
        super("followerArp", "Follows the Context's Chord Progression with an arp pattern",
                Parameter.of(PATTERN, "Pattern", 1, "Arp pattern",
                        TEXT, false, null),
                Parameter.of(STARTING_OCTAVE, "Starting octave", 2, "Starting octave",
                        INTEGER, true, BASE_OCTAVE)
        );
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final Length totalLength = lengthFromNumberOfMeasures(context, arguments);
        final String pattern = (String) arguments.get(PATTERN);
        final List<ChordEvent> chordEvents = context.chordProgression().events();
        final int startingOctave = (int)arguments.get(STARTING_OCTAVE);

        final EventAccumulator accumulator = EventAccumulator.of(totalLength, SHORTEN);
        while(!accumulator.fulfilled()) {
            for (ChordEvent e : chordEvents) {
                final List<Pitch> pitches = e.chord().pitches(startingOctave);
                accumulator.fillWith(Arpeggios.arpeggiate(pitches, pattern), e.length());
                if (accumulator.fulfilled()) break;
            }
        }

        return accumulator.accumulated();
    }

    public static final String PATTERN = "pattern";
    public static final String STARTING_OCTAVE = "startingOctave";
}
