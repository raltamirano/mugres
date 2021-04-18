package mugres.core.filter.builtin.misc;

import mugres.core.common.Context;
import mugres.core.common.Signal;
import mugres.core.common.Signals;
import mugres.core.filter.Filter;

import java.util.*;

public class Latch extends Filter {
    private String lastLatched = null;
    private final List<Signal> latchedSignals = new ArrayList<>();


    public Latch() {
        super("Latch");
    }

    @Override
    protected boolean internalCanHandle(Context context, Signals signals, Map<String, Object> arguments) {
        return true;
    }

    @Override
    protected Signals internalHandle(Context context, Signals signals, Map<String, Object> arguments) {
        final Signals result = Signals.create();
        final String latchKey = latchKey(signals);

        if (latchKey.isEmpty())
            return result;

        for(Signal s : latchedSignals)
            result.add(s.toOff());
        latchedSignals.clear();

        if (latchKey.equals(lastLatched)) {
            lastLatched = null;
        } else {
            lastLatched = latchKey;
            for(Signal s : signals.signals()) {
                if (s.isActive()) {
                    result.add(s);
                    latchedSignals.add(s);
                }
            }
        }

        return result;
    }

    private static String latchKey(final Signals signals) {
        final List<String> keys = new ArrayList<>();
        final List<Signal> sorted = new ArrayList<>(signals.signals());
        Collections.sort(sorted, LATCH_COMPARATOR);

        for(final Signal in : sorted)
            if (in.isActive())
                keys.add(latchKey(in));

        return String.join("*", keys);
    }

    private static String latchKey(final Signal signal) {
        return String.format("%d-%d", signal.getChannel(), signal.getPlayed().getPitch().getMidi());
    }


    private static final Comparator<Signal> LATCH_COMPARATOR =
            Comparator.comparingInt(a -> (a.getChannel() * 1000) + a.getPlayed().getPitch().getMidi());
}
