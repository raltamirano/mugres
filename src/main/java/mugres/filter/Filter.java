package mugres.filter;

import mugres.common.*;
import mugres.filter.builtin.chords.Chorder;
import mugres.filter.builtin.misc.*;
import mugres.filter.builtin.scales.ScaleEnforcer;
import mugres.filter.builtin.system.Monitor;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;

public abstract class Filter {
    protected final Map<String, Object> arguments;

    protected Filter(final Map<String, Object> arguments) {
        this.arguments = arguments == null ? emptyMap() : arguments;
    }

    public abstract String name();

    protected abstract boolean internalCanHandle(final Context context, final Signals signals);

    protected abstract Signals internalHandle(final Context context, final Signals signals);

    protected int getTempo(final Context context) {
        try {
            final Integer tempo = arguments.containsKey("tempo") ?
                    Integer.valueOf(arguments.get("tempo").toString()) :
                    context.tempo();

            return tempo > 0 ?
                    tempo :
                    context.tempo();
        } catch (final Throwable ignore) {
            return context.tempo();
        }
    }

    protected Key getKey(final Context context) {
        try {
            return arguments.containsKey("key") ?
                    Key.fromLabel(arguments.get("key").toString()) :
                    context.key();
        } catch (final Throwable ignore) {
            return context.key();
        }
    }

    protected TimeSignature getTimeSignature(final Context context) {
        try {
            return arguments.containsKey("timeSignature") ?
                    TimeSignature.of(arguments.get("timeSignature").toString()) :
                    context.timeSignature();
        } catch (final Throwable ignore) {
            return context.timeSignature();
        }
    }

    public boolean canHandle(final Context context, final Signals signals) {
        final boolean tagFilter = checkTagFilter(context, signals);
        return tagFilter && internalCanHandle(context, signals);
    }

    public Signals handle(final Context context, final Signals signals) {
        final SplitByTagsResult splitByTagsResult = splitByTags(signals);
        final Signals handledSignals = internalHandle(context, splitByTagsResult.inside());
        handledSignals.addAll(splitByTagsResult.outside());
        return handledSignals;
    }

    private boolean checkTagFilter(final Context context, final Signals signals) {
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

    private SplitByTagsResult splitByTags(final Signals signals) {
        try {
            if (!arguments.containsKey(TAG_FILTER))
                return SplitByTagsResult.of(signals, Signals.create());

            final Signals inside = Signals.create();
            final Signals outside = Signals.create();

            final Set<String> tags = new HashSet<>(asList(arguments.get(TAG_FILTER).toString().split(TAG_FILTER_SEPARATOR)));
            for(final Signal in : signals.signals()) {
                boolean check = false;
                for (final String tag : tags) {
                    if (in.hasTag(tag)) {
                        check = true;
                        inside.add(in);
                        break;
                    }
                }
                if (!check)
                    outside.add(in);
            }

            return SplitByTagsResult.of(inside, outside);
        } catch (final Throwable ignore) {
            return SplitByTagsResult.of(Signals.create(), signals);
        }
    }

    public final Signals accept(final Context context, final Signals signals) {
        return canHandle(context, signals) ?
                handle(context, signals) : signals;
    }

    private static final Map<String, Class<? extends Filter>> REGISTRY = new HashMap<>();
    private static final String TAG_FILTER = "onlyForTags";
    private static final String TAG_FILTER_SEPARATOR = ",";

    static {
        register(Monitor.NAME, Monitor.class);
        register(ScaleEnforcer.NAME, ScaleEnforcer.class);
        register(Chorder.NAME, Chorder.class);
        register(Latch.NAME, Latch.class);
        register(Transpose.NAME, Transpose.class);
        register(Ranges.NAME, Ranges.class);
        register(Clear.NAME, Clear.class);
        register(Splitter.NAME, Splitter.class);
        register(Randomizer.NAME, Randomizer.class);
    }

    public static synchronized void register(final String name, final Class<? extends Filter> clazz) {
                if (REGISTRY.containsKey(name))
            throw new IllegalArgumentException("Already registered filter class: " + name);
        REGISTRY.put(name, clazz);
    }

    public static Filter of(final String name) {
        return of(name, emptyMap());
    }

    public static Filter of(final String name, final Map<String, Object> arguments) {
        if (!REGISTRY.containsKey(name))
            throw new IllegalArgumentException("Invalid filter: " + name);

        try {
            return REGISTRY.get(name).getDeclaredConstructor(Map.class).newInstance(arguments);
        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static class SplitByTagsResult {
        private final Signals inside;
        private final Signals outside;

        private SplitByTagsResult(final Signals inside, final Signals outside) {
            this.inside = inside;
            this.outside = outside;
        }

        public static SplitByTagsResult of(final Signals inside, final Signals outside) {
            return new SplitByTagsResult(inside, outside);
        }

        public Signals inside() {
            return inside;
        }

        public Signals outside() {
            return outside;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            builder.append("Inside: ").append("\n").append(inside).append("\n");
            builder.append("Outside: ").append("\n").append(outside).append("\n");

            return builder.toString();
        }
    }
}
