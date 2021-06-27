package mugres.core.filter.builtin.chords;

import mugres.core.common.*;
import mugres.core.common.chords.Chord;
import mugres.core.common.chords.Type;
import mugres.core.filter.Filter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static mugres.core.common.chords.Type.CUSTOM;
import static mugres.core.utils.Randoms.random;

public class Chorder extends Filter {
    public static final String NAME = "Chorder";
    private static final int DEFAULT_NUMBER_OF_NOTES = 3;

    public Chorder(final Map<String, Object> arguments) {
        super(arguments);
    }

    @Override
    public String getName() {
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
            final Chord chord;
            final List<Pitch> chordPitches;
            switch (getChordMode(arguments)) {
                case DIATONIC:
                    final Key key = getKey(context);
                    if (key.notes().contains(in.getPlayed().pitch().getNote())) {
                        final int numberOfNotes = getNumberOfNotes(arguments);
                        chordPitches = key.chord(in.getPlayed().pitch(), numberOfNotes);
                    } else {
                        // Discard notes outside the key
                        chordPitches = emptyList();
                    }
                    break;
                case FIXED:
                    chordPitches = Chord.of(in.getPlayed().pitch().getNote(), getChordType(arguments))
                            .pitches(in.getPlayed().pitch().getOctave());
                    ;
                    break;
                case RANDOM:
                    chordPitches = Chord.of(in.getPlayed().pitch().getNote(), random(Arrays.asList(Type.values()), CUSTOM))
                            .pitches(in.getPlayed().pitch().getOctave());
                    ;
                    break;
                default:
                    chordPitches = emptyList();
                    // TODO: logging!
            }

            if (!chordPitches.isEmpty()) {
                for (final Pitch pitch : chordPitches) {
                    result.add(in.modifiedPlayed(Played.of(pitch, in.getPlayed().velocity())));
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
