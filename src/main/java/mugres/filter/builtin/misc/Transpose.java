package mugres.filter.builtin.misc;

import mugres.common.Context;
import mugres.common.Played;
import mugres.common.Signal;
import mugres.common.Signals;
import mugres.filter.Filter;

import java.util.Map;

import static mugres.common.Pitch.isValidMidiNoteNumber;

public class Transpose extends Filter {
    public static final String NAME = "Transpose";

    public Transpose(final Map<String, Object> arguments) {
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
        final int semitones = getSemitonesToTranspose(arguments);

        for(final Signal in : signals.signals())
            result.add(in.modifiedPlayed(transpose(in.played(), semitones)));

        return result;
    }

    private int getSemitonesToTranspose(final Map<String, Object> arguments) {
        try {
            return arguments.containsKey("semitones") ?
                    Double.valueOf(arguments.get("semitones").toString()).intValue() :
                    0;
        } catch (final Throwable ignore) {
            return 0;
        }
    }

    private Played transpose(final Played played, final int semitones) {
        if (semitones == 0)
            return played;

        final int target = played.pitch().midi() + semitones;
        if (!isValidMidiNoteNumber(target))
            return played;

        return semitones > 0 ? played.pitchUp(semitones) : played.pitchDown(-semitones);
    }
}
