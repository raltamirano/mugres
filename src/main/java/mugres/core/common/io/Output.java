package mugres.core.common.io;

import mugres.core.common.InstrumentChange;
import mugres.core.common.Signal;

import javax.sound.midi.Receiver;

public interface Output {
    void send(final Signal signal);
    void send(final InstrumentChange instrumentChange);

    static Output midiOutput(final Receiver midiOutputPort) {
        return new MidiOutput(midiOutputPort);
    }
}
