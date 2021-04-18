package mugres.core.filter;

import mugres.core.common.*;
import mugres.core.filter.builtin.arp.Arpeggiate;
import mugres.core.filter.builtin.chords.Chorder;
import mugres.core.filter.builtin.misc.Clear;
import mugres.core.filter.builtin.misc.Latch;
import mugres.core.filter.builtin.misc.Ranges;
import mugres.core.filter.builtin.misc.Transpose;
import mugres.core.filter.builtin.scales.ScaleEnforcer;
import mugres.core.filter.builtin.system.Monitor;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;

public abstract class Filter {
    private final String name;

    protected Filter(final String name) {
        this.name = name;

        register(this);
    }

    public String getName() {
        return name;
    }

    protected abstract boolean internalCanHandle(final Context context, final Signals signals, final Map<String, Object> arguments);

    protected abstract Signals internalHandle(final Context context, final Signals signals, final Map<String, Object> arguments);

    protected static int getTempo(final Context context, final Map<String, Object> arguments) {
        try {
            final Integer tempo = arguments.containsKey("tempo") ?
                    Integer.valueOf(arguments.get("tempo").toString()) :
                    context.getTempo();

            return tempo > 0 ?
                    tempo :
                    context.getTempo();
        } catch (final Throwable ignore) {
            return context.getTempo();
        }
    }

    protected static Key getKey(final Context context, final Map<String, Object> arguments) {
        try {
            return arguments.containsKey("key") ?
                    Key.fromLabel(arguments.get("key").toString()) :
                    context.getKey();
        } catch (final Throwable ignore) {
            return context.getKey();
        }
    }

    protected static TimeSignature getTimeSignature(final Context context, final Map<String, Object> arguments) {
        try {
            return arguments.containsKey("timeSignature") ?
                    TimeSignature.of(arguments.get("timeSignature").toString()) :
                    context.getTimeSignature();
        } catch (final Throwable ignore) {
            return context.getTimeSignature();
        }
    }

    public boolean canHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        final boolean tagFilter = checkTagFilter(context, signals, arguments);
        return tagFilter && internalCanHandle(context, signals, arguments);
    }

    public Signals handle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        return internalHandle(context, filterByTags(context, signals, arguments), arguments);
    }

    private boolean checkTagFilter(final Context context, final Signals signals, final Map<String, Object> arguments) {
        try {
            if (!arguments.containsKey(TAG_FILTER))
                return true;

            final Set<String> tags = new HashSet<>(asList(arguments.get(TAG_FILTER).toString().split(TAG_FILTER_SEPARATOR)));
            for(final String tag : tags)
                if (signals.signals().stream().anyMatch(s -> s.hasTag(tag)))
                    return true;

            return false;
        } catch (final Throwable ignore) {
            // TODO: logging
            return false;
        }
    }

    private Signals filterByTags(final Context context, final Signals signals, final Map<String, Object> arguments) {
        try {
            if (!arguments.containsKey(TAG_FILTER))
                return signals;

            final Signals result = Signals.create();
            final Set<String> tags = new HashSet<>(asList(arguments.get(TAG_FILTER).toString().split(TAG_FILTER_SEPARATOR)));
            for(final Signal in : signals.signals()) {
                for (final String tag : tags) {
                    if (in.hasTag(tag)) {
                        result.add(in);
                        break;
                    }
                }
            }

            return result;
        } catch (final Throwable ignore) {
            // TODO: logging
            return Signals.create();
        }
    }

    public final Signals accept(final Context context, final Signals signals) {
        return accept(context, signals, emptyMap());
    }

    public final Signals accept(final Context context, final Signals signals, final Map<String, Object> arguments) {
        return canHandle(context, signals, arguments == null ? emptyMap() : arguments) ?
                handle(context, signals, arguments == null ? emptyMap() : arguments) : signals;
    }

    private static final Map<String, Filter> REGISTRY = new HashMap<>();
    private static final String TAG_FILTER = "onlyForTags";
    private static final String TAG_FILTER_SEPARATOR = ",";

    static {
        new Monitor();
        new ScaleEnforcer();
        new Chorder();
        new Latch();
        new Arpeggiate();
        new Transpose();
        new Ranges();
        new Clear();
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
