package mugres.function.builtin.follower;

import mugres.common.Context;
import mugres.common.DataType;
import mugres.common.Length;
import mugres.common.Rhythm;
import mugres.common.chords.ChordMode;
import mugres.common.chords.ChordProgression.ChordEvent;
import mugres.function.Function.EventsFunction;
import mugres.function.utils.EventAccumulator;
import mugres.parametrizable.Parameter;
import mugres.tracker.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static mugres.common.DataType.CHORD_MODE;
import static mugres.common.DataType.INTEGER;
import static mugres.common.DataType.LENGTH;
import static mugres.common.Pitch.BASE_OCTAVE;
import static mugres.common.Pitch.DEFAULT_VELOCITY;
import static mugres.function.utils.EventAccumulator.OnExcessAction.SHORTEN;

public class FollowerChord extends EventsFunction {
    public FollowerChord() {
        super("followerChord", "Follows the Context's Chord Progression chords",
                Parameter.of(STARTING_OCTAVE, "Starting octave", 1, "Starting octave",
                        DataType.INTEGER, true, null),
                Parameter.of(FOLLOW_CHORDS_MODE, "Chord Mode", 2, "Follow chord mode",
                        DataType.CHORD_MODE, true, ChordMode.FULL),
                Parameter.of(SEPARATION, "Separation between notes", 3, "Separation between notes, " +
                                "in ticks. Not meant to  arpeggiate, just to give more expression options",
                        DataType.LENGTH, true, Length.ZERO),
                Parameter.of(RHYTHM, "Rhythm", 4, "Rhythm",
                        DataType.RHYTHM, true, null)
        );
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final Length totalLength = lengthFromNumberOfMeasures(context, arguments);
        final List<ChordEvent> chordEvents = context.chordProgression().events();
        final Integer startingOctave = (Integer)arguments.get(STARTING_OCTAVE);
        final ChordMode chordMode = (ChordMode)arguments.get(FOLLOW_CHORDS_MODE);
        final Length separation = (Length)arguments.get(SEPARATION);
        final Rhythm rhythm = (Rhythm)arguments.get(RHYTHM);

        final EventAccumulator accumulator = EventAccumulator.of(totalLength, SHORTEN);
        while(!accumulator.fulfilled()) {
            for (ChordEvent e : chordEvents) {
                final List<Event> events = new ArrayList<>();
                if (chordMode == ChordMode.ROOT) {
                    events.add(Event.of(Length.ZERO,
                            startingOctave != null ? e.chord().root().pitch(startingOctave) : e.chord().root().pitch(),
                            e.length(), DEFAULT_VELOCITY));
                    if (rhythm != null) {
                        accumulator.fillWith(rhythm.applyToEvents(events), e.length(), false, Length.ZERO);
                    } else {
                        accumulator.offer(events, false, Length.ZERO);
                    }
                } else {
                    (startingOctave != null ? e.chord().pitches(startingOctave) : e.chord().pitches())
                            .forEach(p -> events.add(Event.of(Length.ZERO, p, e.length(), DEFAULT_VELOCITY)));
                    if (rhythm != null) {
                        accumulator.fillWithChords(rhythm.applyToEventsAsChord(events), e.length(), separation);
                    } else {
                        accumulator.offer(events, true, separation);
                    }
                }
                if (accumulator.fulfilled()) break;
            }
        }

        return accumulator.accumulated();
    }

    public static final String STARTING_OCTAVE = "startingOctave";
    public static final String FOLLOW_CHORDS_MODE = "followChordsMode";
    public static final String SEPARATION = "separation";
    public static final String RHYTHM = "rhythm";
}
