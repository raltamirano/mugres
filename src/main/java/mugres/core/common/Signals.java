package mugres.core.common;

import java.util.*;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static mugres.core.common.Signal.TAGS;

public class Signals {
    private final List<Signal> signals = new ArrayList<>();
    private Map<String, Object> attributes;
    private final Object attributesSyncObject = new Object();

    private Signals(final Signal...signals) {
        this.signals.addAll(asList(signals));
    }

    private Signals(final List<Signal> signals) {
        this.signals.addAll(signals);
    }

    public static Signals of(final Signal... signals) {
        return new Signals(signals);
    }

    public static Signals of(final List<Signal> signals) {
        return new Signals(signals);
    }

    public static Signals create() {
        return new Signals();
    }

    public void add(final Signal signal) {
        if (signal == null)
            throw new IllegalArgumentException("signal");

        signals.add(signal);
    }

    public void addAll(final Signals others) {
        others.signals.forEach(this::add);
    }

    public List<Signal> signals() {
        return Collections.unmodifiableList(signals);
    }

    public Signals actives() {
        return of(signals.stream().filter(Signal::isActive).toArray(Signal[]::new));
    }

    public Signals inactives() {
        return of(signals.stream().filter(s -> !s.isActive()).toArray(Signal[]::new));
    }

    public void forEach(final Consumer<? super Signal> consumer) {
        signals.forEach(consumer);
    }

    public Signal first() {
        return signals.get(0);
    }

    public boolean isEmpty() {
        return signals.isEmpty();
    }

    public Map<String, Object> getAttributes() {
        synchronized (attributesSyncObject) {
            return  attributes == null ? Collections.emptyMap() : Collections.unmodifiableMap(attributes);
        }
    }

    public void setAttribute(final String name, final Object value) {
        synchronized (attributesSyncObject) {
            if (attributes == null)
                attributes = new HashMap<>();
            attributes.put(name, value);
        }
    }

    public <X> X getAttribute(final String name) {
        synchronized (attributesSyncObject) {
            return attributes == null ?
                    null :
                    (X)attributes.get(name);
        }
    }

    public void removeAttribute(final String name) {
        synchronized (attributesSyncObject) {
            if (attributes != null)
                attributes.remove(name);
        }
    }

    public void addTag(final String tag) {
        if (tag == null || tag.trim().isEmpty())
            return;

        synchronized (attributesSyncObject) {
            Set<String> tags = getAttribute(TAGS);
            if (tags == null) {
                tags = new HashSet<>();
                setAttribute(TAGS, tags);
            }
            tags.add(tag);
        }
    }

    public boolean hasTag(final String tag) {
        if (tag == null || tag.trim().isEmpty())
            return false;

        final Set<String> tags = getAttribute(TAGS);
        return tags == null ? false : tags.contains(tag);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for(final Signal s : signals)
            builder.append(s).append("\n");
        return builder.toString();
    }
}
