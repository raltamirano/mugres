package mugres.filter.builtin.chords;

import mugres.common.*;
import mugres.common.chords.Chord;
import mugres.common.chords.Type;
import mugres.filter.Filter;
import mugres.live.Signal;
import mugres.live.Signals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static mugres.common.chords.Type.CUSTOM;
import static mugres.utils.Randoms.random;

public class Chorder extends Filter {
    public static final String NAME = "Chorder";
    private static final int DEFAULT_NUMBER_OF_NOTES = 3;

    public Chorder(final Map<String, Object> arguments) {
        super(arguments);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected boolean internalCanHandle(final Context context, final Signals signals) {
        return true;
    }

    @Override
    protected Signals internalHandle(final Context context, final Signals signals) {
        final Signals result = Signals.create();

        for(final Signal in : signals.signals()) {
            final List<Pitch> chordPitches;
            switch (getChordMode(arguments)) {
                case DIATONIC:
                    final Key key = getKey(context);
                    if (key.notes().contains(in.pitch().note())) {
                        final int numberOfNotes = getNumberOfNotes(arguments);
                        chordPitches = key.chord(in.pitch(), numberOfNotes);
                    } else {
                        // Discard notes outside the key
                        chordPitches = emptyList();
                    }
                    break;
                case FIXED:
                    chordPitches = Chord.of(in.pitch().note(), getChordType(arguments))
                            .pitches(in.pitch().octave());
                    break;
                case RANDOM:
                    chordPitches = Chord.of(in.pitch().note(), random(Arrays.asList(Type.values()), CUSTOM))
                            .pitches(in.pitch().octave());
                    break;
                default:
                    chordPitches = emptyList();
                    // TODO: logging!
            }

            if (!chordPitches.isEmpty()) {
                for (final Pitch pitch : chordPitches) {
                    result.add(in.modifiedPitch(pitch));
                }
            }
        }

        return result;
    }

    private int getNumberOfNotes(final Map<String, Object> arguments) {
        try {
            final int notes = arguments.containsKey("notes") ?
                    Integer.valueOf(arguments.get("notes").toString()) :
                    DEFAULT_NUMBER_OF_NOTES;
            return notes > 0 && notes <= 48 ? notes : DEFAULT_NUMBER_OF_NOTES;
        } catch(final Throwable ignore) {
            return DEFAULT_NUMBER_OF_NOTES;
        }
    }

    private ChordMode getChordMode(final Map<String, Object> arguments) {
        return ChordMode.valueOf(arguments.get("chordMode").toString());
    }

    private Type getChordType(final Map<String, Object> arguments) {
        return Type.forAbbreviation(arguments.get("chordType").toString());
    }

    public enum ChordMode {
        DIATONIC,
        FIXED,
        RANDOM
    }
}
