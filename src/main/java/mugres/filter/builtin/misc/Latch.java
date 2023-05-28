package mugres.filter.builtin.misc;

import mugres.common.Context;
import mugres.common.Pitch;
import mugres.filter.Filter;
import mugres.live.Signal;
import mugres.live.Signals;

import java.util.HashMap;
import java.util.Map;

public class Latch extends Filter {
    public static final String NAME = "Latch";
    private final Map<Integer, Signal> latchedSignals = new HashMap<>();
    private Pitch killSwitch;

    public Latch(final Map<String, Object> arguments) {
        super(arguments);

        killSwitch = getKillSwitch(arguments);
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
        final Signals noteOns = signals.noteOns();

        final Signals result = Signals.create();

        if (noteOns.isMono() && noteOns.hasPitch(killSwitch)) {
            for(Signal s : latchedSignals.values())
                result.add(s.toNoteOff());
            latchedSignals.clear();
        } else {
            for (Signal s : noteOns.signals()) {
                if (latchedSignals.containsKey(s.discriminator())) {
                    result.add(s.toNoteOff());
                    latchedSignals.remove(s.discriminator());
                } else {
                    result.add(s);
                    latchedSignals.put(s.discriminator(), s);
                }
            }
        }

        return result;
    }

    private Pitch getKillSwitch(final Map<String, Object> arguments) {
        try {
            if (arguments.containsKey("killSwitch"))
                return Pitch.of(Integer.valueOf(arguments.get("killSwitch").toString()));
            return null;
        } catch (final Exception ignore) {
            return null;
        }
    }
}
