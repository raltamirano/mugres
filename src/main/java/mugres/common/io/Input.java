package mugres.common.io;

import mugres.common.ControlChange;
import mugres.common.InstrumentChange;
import mugres.live.Signal;

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

    public void receive(final Signal signal) {
        listeners.forEach(listener -> listener.receive(signal));
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
