package mugres.core.filter;

import mugres.core.common.*;
import mugres.core.filter.builtin.arp.Arpeggiate;
import mugres.core.filter.builtin.chords.Chorder;
import mugres.core.filter.builtin.misc.*;
import mugres.core.filter.builtin.scales.ScaleEnforcer;
import mugres.core.filter.builtin.system.In;
import mugres.core.filter.builtin.system.Monitor;
import mugres.core.filter.builtin.system.Out;

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
        final SplitByTagsResult splitByTagsResult = splitByTags(signals, arguments);
        final Signals handledSignals = internalHandle(context, splitByTagsResult.getInside(), arguments);
        handledSignals.addAll(splitByTagsResult.getOutside());
        return handledSignals;
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

    private SplitByTagsResult splitByTags(final Signals signals, final Map<String, Object> arguments) {
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
        new Splitter();
        new Randomizer();
    }

    private static synchronized void register(final Filter filter) {
        if (filter instanceof In) return;
        if (filter instanceof Out) return;

        final String name = filter.getName();
        if (REGISTRY.containsKey(name))
            throw new IllegalArgumentException("Already registered filter: " + name);
        REGISTRY.put(name, filter);
    }

    public static Filter forName(final String name) {
        return REGISTRY.get(name);
    }

    public static void activateSignal(final int channel, final Pitch pitch, final UUID eventId) {
        SIGNALS[channel-1][pitch.getMidi()] = eventId;
        fireActivatedSignalNotification(eventId, channel, pitch);
    }

    public static void deactivateSignal(final int channel, final Pitch pitch) {
        final UUID originalEventId = SIGNALS[channel-1][pitch.getMidi()];
        SIGNALS[channel-1][pitch.getMidi()] = null;
        if (originalEventId != null)
            fireDeactivatedSignalNotification(originalEventId, channel, pitch);
    }

    public static void addSignalEventListener(final SignalEventListener listener) {
        SIGNAL_EVENT_LISTENERS.add(listener);
    }

    public static boolean isEventActive(final UUID eventId) {
        return ACTIVE_EVENTS.contains(eventId);
    }

    public static boolean isSignalActive(final int channel, final Pitch pitch) {
        return SIGNALS[channel-1][pitch.getMidi()] != null;
    }

    private static void fireActivatedSignalNotification(final UUID eventId, final int channel, final Pitch pitch) {
        ACTIVE_EVENTS.add(eventId);
        SIGNAL_EVENT_LISTENERS.forEach(l -> l.activated(eventId, channel, pitch));
    }

    private static void fireDeactivatedSignalNotification(final UUID eventId, final int channel, final Pitch pitch) {
        ACTIVE_EVENTS.remove(eventId);
        SIGNAL_EVENT_LISTENERS.forEach(l -> l.deactivated(eventId, channel, pitch));
    }

    private static final Set<SignalEventListener> SIGNAL_EVENT_LISTENERS = new HashSet<>();
    private static final UUID[][] SIGNALS = new UUID[16][128];
    private static final Set<UUID> ACTIVE_EVENTS = new HashSet<>();

    public interface SignalEventListener {
        void activated(final UUID activated, final int channel, final Pitch pitch);
        void deactivated(final UUID deactivated, final int channel, final Pitch pitch);
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

        public Signals getInside() {
            return inside;
        }

        public Signals getOutside() {
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
