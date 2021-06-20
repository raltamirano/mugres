package mugres.core.common.io;

import mugres.core.common.InstrumentChange;
import mugres.core.common.Signal;

import javax.sound.midi.Transmitter;
import java.util.HashSet;
import java.util.Set;

public abstract class Input {
    private final Set<Listener> listeners = new HashSet<>();

    public static Input midiInput(final Transmitter inputPort) {
        return new MidiInput(inputPort);
    }

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

    public interface Listener {
        void receive(final Signal signal);
        void receive(final InstrumentChange instrumentChange);
    }
}
