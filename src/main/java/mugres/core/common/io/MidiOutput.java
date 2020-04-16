package mugres.core.common.io;

import mugres.core.common.Signal;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import static javax.sound.midi.ShortMessage.NOTE_ON;

public class MidiOutput implements Output {
    private final Receiver midiOutputPort;

    public MidiOutput(final Receiver midiOutputPort) {
        this.midiOutputPort = midiOutputPort;
    }

    @Override
    public void send(final Signal signal) {
        try {
            final ShortMessage message = new ShortMessage(NOTE_ON, signal.getChannel(),
                    signal.getPlayed().getPitch().getMidi(), signal.getPlayed().getVelocity());
            midiOutputPort.send(message, -1);
        } catch (final InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public Receiver getMidiOutputPort() {
        return midiOutputPort;
    }
}
