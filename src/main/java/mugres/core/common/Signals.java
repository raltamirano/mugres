package mugres.core.common;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Signals {
    private final List<Signal> signals = new ArrayList<>();
    private Map<String, Object> attributes;
    private final Object attributesSyncObject = new Object();

    private Signals(final Signal...signals) {
        this.signals.addAll(asList(signals));
    }

    public static Signals of(final Signal... signals) {
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

    public List<Signal> signals() {
        return Collections.unmodifiableList(signals);
    }

    public Signals actives() {
        return of(signals.stream().filter(Signal::isActive).toArray(Signal[]::new));
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

    public Object getAttribute(final String name) {
        synchronized (attributesSyncObject) {
            return attributes == null ?
                    null :
                    attributes.get(name);
        }
    }

    public void removeAttribute(final String name) {
        synchronized (attributesSyncObject) {
            if (attributes != null)
                attributes.remove(name);
        }
    }
}
