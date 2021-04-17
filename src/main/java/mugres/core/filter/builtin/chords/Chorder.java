package mugres.core.filter.builtin.chords;

import mugres.core.common.*;
import mugres.core.common.chords.Chord;
import mugres.core.common.chords.Type;
import mugres.core.filter.Filter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static mugres.core.common.chords.Type.CUSTOM;
import static mugres.core.utils.Randoms.random;

public class Chorder extends Filter {
    private static final int DEFAULT_NUMBER_OF_NOTES = 3;

    public Chorder() {
        super("Chorder");
    }

    @Override
    protected boolean canHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        return true;
    }

    @Override
    protected Signals handle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        final Signal in = signals.first();
        final Signals result = Signals.create();

        final Chord chord;
        final List<Pitch> chordPitches;
        switch(getChordMode(arguments)) {
            case DIATONIC:
                final Key key = getKey(context, arguments);
                if (key.notes().contains(in.getPlayed().getPitch().getNote())) {
                    final int numberOfNotes = getNumberOfNotes(arguments);
                    chordPitches = key.chord(in.getPlayed().getPitch(), numberOfNotes);
                } else {
                    // Discard notes outside the key
                    chordPitches = emptyList();
                }
                break;
            case FIXED:
                chordPitches = Chord.of(in.getPlayed().getPitch().getNote(), getChordType(arguments))
                        .pitches(in.getPlayed().getPitch().getOctave());;
                break;
            case RANDOM:
                chordPitches = Chord.of(in.getPlayed().getPitch().getNote(), random(Arrays.asList(Type.values()), CUSTOM))
                        .pitches(in.getPlayed().getPitch().getOctave());;
                break;
            default:
                chordPitches = emptyList();
                // TODO: logging!
        }

        if (!chordPitches.isEmpty()) {

            for(final Pitch pitch : chordPitches) {
                result.add(in.modifiedPlayed(Played.of(pitch, in.getPlayed().getVelocity())));
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

    private Key getKey(final Context context, final Map<String, Object> arguments) {
        return Key.fromLabel(arguments.get("key").toString());
    }


    public enum ChordMode {
        DIATONIC,
        FIXED,
        RANDOM
    }
}
