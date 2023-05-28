package mugres.common.io;

import mugres.common.Context;
import mugres.common.ControlChange;
import mugres.common.InstrumentChange;
import mugres.filter.Filter;
import mugres.live.Signal;
import mugres.live.Signals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Input {
    private final List<Filter> filters = new ArrayList<>();
    private final Set<Listener> listeners = new HashSet<>();

    protected List<Filter> filters() {
        return filters;
    }

    public final void addFilter(final Filter filter) {
        if (filter != null)
            filters.add(filter);
    }

    public final void addListener(final Listener listener) {
        if (listener != null)
            listeners.add(listener);
    }

    public final void removeListener(final Listener listener) {
        if (listener != null)
            listeners.remove(listener);
    }

    public void receive(final Signal signal) {
        if (filters.isEmpty()) {
            listeners.forEach(listener -> listener.receive(signal));
        } else {
            Signals signals = Signals.of(signal);

            // Pass through every user-defined filter
            for(final Filter filter : filters)
                signals = filter.accept(Context.basicContext(), signals);

            for(Signal s : signals.signals())
                listeners.forEach(listener -> listener.receive(signal));
        }
    }

    public void receive(final InstrumentChange instrumentChange) {
        listeners.forEach(listener -> listener.receive(instrumentChange));
    }

    public void receive(final ControlChange controlChange) {
        listeners.forEach(listener -> listener.receive(controlChange));
    }

    public interface Listener {
        void receive(final Signal signal);
        void receive(final InstrumentChange instrumentChange);
        void receive(final ControlChange controlChange);
    }
}
