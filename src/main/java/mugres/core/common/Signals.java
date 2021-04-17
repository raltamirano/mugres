package mugres.core.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Signals {
    private final List<Signal> signals = new ArrayList<>();

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
}
