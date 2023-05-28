package mugres.common.io;

import mugres.common.ControlChange.MidiControlChange;
import mugres.common.Instrument;
import mugres.common.InstrumentChange;
import mugres.common.Pitch;
import mugres.live.Signal;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import static javax.sound.midi.ShortMessage.CONTROL_CHANGE;
import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;
import static javax.sound.midi.ShortMessage.PROGRAM_CHANGE;

public class MidiInput extends Input {
    private final Transmitter transmitter;

    private MidiInput(final Transmitter transmitter) {
        this.transmitter = transmitter;
        this.transmitter.setReceiver(new MidiReceiver());
    }

    public static MidiInput of(final Transmitter inputPort) {
        return new MidiInput(inputPort);
    }

    private class MidiReceiver implements Receiver {
        @Override
        public void send(final MidiMessage message, long timeStamp) {
            if (!(message instanceof ShortMessage))
                return;
            final ShortMessage shortMessage = (ShortMessage) message;

            if (shortMessage.getCommand() == NOTE_ON)
                MidiInput.this.receive(Signal.on(shortMessage.getChannel() + 1,
                        Pitch.of(shortMessage.getData1()), shortMessage.getData2()));
            else if (shortMessage.getCommand() == NOTE_OFF)
                MidiInput.this.receive(Signal.off(shortMessage.getChannel() + 1,
                        Pitch.of(shortMessage.getData1())));
            else if (shortMessage.getCommand() == PROGRAM_CHANGE)
                MidiInput.this.receive(InstrumentChange.of(shortMessage.getChannel() + 1,
                        Instrument.of(shortMessage.getData1())));
            else if (shortMessage.getCommand() == CONTROL_CHANGE)
                MidiInput.this.receive(MidiControlChange.of(shortMessage.getChannel() + 1,
                        shortMessage.getData1(), shortMessage.getData2()));
        }

        @Override
        public void close() {
        }
    }
}
