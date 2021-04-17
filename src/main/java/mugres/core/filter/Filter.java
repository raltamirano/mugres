package mugres.core.filter;

import mugres.core.common.Context;
import mugres.core.common.Key;
import mugres.core.common.Signals;
import mugres.core.common.TimeSignature;
import mugres.core.filter.builtin.arp.Arpeggiate;
import mugres.core.filter.builtin.chords.Chorder;
import mugres.core.filter.builtin.misc.Latch;
import mugres.core.filter.builtin.system.Monitor;
import mugres.core.filter.builtin.scales.ScaleEnforcer;

import java.util.HashMap;
import java.util.Map;

public abstract class Filter {
    private final String name;

    protected Filter(final String name) {
        this.name = name;

        register(this);
    }

    public String getName() {
        return name;
    }

    protected abstract boolean canHandle(final Context context, final Signals signals, final Map<String, Object> arguments);

    protected abstract Signals handle(final Context context, final Signals signals, final Map<String, Object> arguments);

    protected static int getTempo(final Context context, final Map<String, Object> arguments) {
        try {
            final Integer tempo = Integer.valueOf(arguments.get("tempo").toString());
            if (tempo < 0)
                return context.getTempo();
            return tempo;
        } catch (final Throwable ignore) {
            return context.getTempo();
        }
    }

    protected static Key getKey(final Context context, final Map<String, Object> arguments) {
        try {
            return Key.fromLabel(arguments.get("key").toString());
        } catch (final Throwable ignore) {
            return context.getKey();
        }
    }

    protected static TimeSignature getTimeSignature(final Context context, final Map<String, Object> arguments) {
        try {
            return TimeSignature.of(arguments.get("timeSignature").toString());
        } catch (final Throwable ignore) {
            return context.getTimeSignature();
        }
    }

    public final Signals accept(final Context context, final Signals signals) {
        return accept(context, signals, null);
    }

    public final Signals accept(final Context context, final Signals signals, final Map<String, Object> arguments) {
        return canHandle(context, signals, arguments) ?
                handle(context, signals, arguments) : signals;
    }

    private static final Map<String, Filter> REGISTRY = new HashMap<>();

    static {
        new Monitor();
        new ScaleEnforcer();
        new Chorder();
        new Latch();
        new Arpeggiate();
    }

    private static synchronized void register(final Filter filter) {
        final String name = filter.getName();
        if (REGISTRY.containsKey(name))
            throw new IllegalArgumentException("Already registered filter: " + name);
        REGISTRY.put(name, filter);
    }

    public static Filter forName(final String name) {
        return REGISTRY.get(name);
    }
}
