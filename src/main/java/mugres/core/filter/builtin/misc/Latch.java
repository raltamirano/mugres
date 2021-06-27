package mugres.core.filter.builtin.misc;

import mugres.core.common.Context;
import mugres.core.common.Signal;
import mugres.core.common.Signals;
import mugres.core.filter.Filter;

import java.util.*;

public class Latch extends Filter {
    public static final String NAME = "Latch";
    private String lastLatched = null;
    private final List<Signal> latchedSignals = new ArrayList<>();

    public Latch(final Map<String, Object> arguments) {
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
        return String.format("%d-%d", signal.channel(), signal.played().pitch().midi());
    }


    private static final Comparator<Signal> LATCH_COMPARATOR =
            Comparator.comparingInt(a -> (a.channel() * 1000) + a.played().pitch().midi());
}
