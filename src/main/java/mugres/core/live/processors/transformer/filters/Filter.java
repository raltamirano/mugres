package mugres.core.live.processors.transformer.filters;

import mugres.core.common.Context;
import mugres.core.common.Signals;

import java.util.HashMap;
import java.util.Map;

public abstract class Filter {
    private final String name;

    private Filter next;

    Filter(final String name) {
        this(name, null);
    }

    Filter(final String name, final Filter next) {

        this.name = name;
        this.next = next;

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

    public void setNext(final Filter filter) {
        this.next = filter;
    }

    private static final Map<String, Filter> REGISTRY = new HashMap<>();

    static {
        new Monitor();
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
