package mugres.core.common.io;

import mugres.core.common.Instrument;
import mugres.core.common.InstrumentChange;
import mugres.core.common.Pitch;
import mugres.core.common.Played;
import mugres.core.common.Signal;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
import java.util.UUID;

import static java.lang.System.currentTimeMillis;
import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;
import static javax.sound.midi.ShortMessage.PROGRAM_CHANGE;

public class MidiInput extends Input implements Receiver {
    private final Transmitter transmitter;

    public MidiInput(final Transmitter transmitter) {
        this.transmitter = transmitter;
        this.transmitter.setReceiver(this);
    }

    @Override
    public void send(final MidiMessage message, long timeStamp) {
        if (!(message instanceof ShortMessage))
            return;
        final ShortMessage shortMessage = (ShortMessage) message;

        if (shortMessage.getCommand() == NOTE_ON)
            send(Signal.on(UUID.randomUUID(), currentTimeMillis(),
                    shortMessage.getChannel(),
                    Played.of(Pitch.of(shortMessage.getData1()), shortMessage.getData2())));
        else if (shortMessage.getCommand() == NOTE_OFF)
            send(Signal.off(UUID.randomUUID(), currentTimeMillis(),
                    shortMessage.getChannel(),
                    Played.of(Pitch.of(shortMessage.getData1()), 0)));
        else if (shortMessage.getCommand() == PROGRAM_CHANGE)
            send(InstrumentChange.of(shortMessage.getChannel(), Instrument.of(shortMessage.getData1())));
    }

    @Override
    public void close() {
    }
}
