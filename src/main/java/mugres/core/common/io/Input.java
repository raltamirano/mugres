package mugres.core.common.io;

import mugres.core.common.ControlChange;
import mugres.core.common.InstrumentChange;
import mugres.core.common.Signal;

import java.util.HashSet;
import java.util.Set;

public abstract class Input {
    private final Set<Listener> listeners = new HashSet<>();

    public final void addListener(final Listener listener) {
        if (listener != null)
            listeners.add(listener);
    }

    public final void removeListener(final Listener listener) {
        if (listener != null)
            listeners.remove(listener);
    }

    public void send(final Signal signal) {
        listeners.forEach(listener -> listener.receive(signal));
    }

    public void send(final InstrumentChange instrumentChange) {
        listeners.forEach(listener -> listener.receive(instrumentChange));
    }

    public void send(final ControlChange controlChange) {
        listeners.forEach(listener -> listener.receive(controlChange));
    }

    public interface Listener {
        void receive(final Signal signal);
        void receive(final InstrumentChange instrumentChange);
        void receive(final ControlChange controlChange);
    }
}
