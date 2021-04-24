package mugres.core.common.io;

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
        listeners.add(listener);
    }

    public void send(final Signal signal) {
        listeners.forEach(listener -> listener.receive(signal));
    }

    public interface Listener {
        void receive(final Signal signal);
    }
}
