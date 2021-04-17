package mugres.core.filter;

import mugres.core.common.Context;
import mugres.core.common.Signals;
import mugres.core.filter.builtin.chords.Chorder;
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
