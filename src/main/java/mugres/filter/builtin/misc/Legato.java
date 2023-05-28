package mugres.filter.builtin.misc;

import mugres.common.Context;
import mugres.live.Signal;
import mugres.live.Signals;
import mugres.filter.Filter;

import java.util.*;

public class Legato extends Filter {
    public static final String NAME = "Legato";
    private String lastLatched = null;
    private final List<Signal> latchedSignals = new ArrayList<>();

    public Legato(final Map<String, Object> arguments) {
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
        final Signals noteOns = signals.noteOns();
        if (noteOns.isEmpty())
            return Signals.create();

        final Signals result = Signals.create();
        final String latchKey = latchKey(signals);

        for(Signal l : latchedSignals)
            result.add(l.toNoteOff());

        latchedSignals.clear();

        if (latchKey.equals(lastLatched)) {
            lastLatched = null;
        } else {
            lastLatched = latchKey;
            for (Signal s : noteOns.signals()) {
                result.add(s);
                latchedSignals.add(s);
            }
        }

        return result;
    }

    private static String latchKey(final Signals signals) {
        final List<String> keys = new ArrayList<>();
        final List<Signal> sorted = new ArrayList<>(signals.signals());
        Collections.sort(sorted, LATCH_COMPARATOR);

        for(final Signal in : sorted)
            if (in.isNoteOn())
                keys.add(latchKey(in));

        return String.join("*", keys);
    }

    private static String latchKey(final Signal signal) {
        return String.format("%d-%d", signal.channel(), signal.pitch().midi());
    }


    private static final Comparator<Signal> LATCH_COMPARATOR =
            Comparator.comparingInt(a -> (a.channel() * 1000) + a.pitch().midi());
}
