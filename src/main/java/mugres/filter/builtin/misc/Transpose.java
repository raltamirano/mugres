package mugres.filter.builtin.misc;

import mugres.common.Context;
import mugres.live.Signal;
import mugres.live.Signals;
import mugres.filter.Filter;

import java.util.Map;

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
            result.add(in.modifiedPitch(in.pitch().safeTranspose(semitones)));

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
}
