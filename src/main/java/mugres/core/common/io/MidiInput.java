package mugres.core.common.io;

import mugres.core.common.Pitch;
import mugres.core.common.Played;
import mugres.core.common.Signal;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import static java.lang.System.currentTimeMillis;
import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;

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
            send(Signal.on(currentTimeMillis(),
                    shortMessage.getChannel(),
                    Played.of(Pitch.of(shortMessage.getData1()), shortMessage.getData2())));
        else if (shortMessage.getCommand() == NOTE_OFF)
            send(Signal.off(currentTimeMillis(),
                    shortMessage.getChannel(),
                    Played.of(Pitch.of(shortMessage.getData1()), 0)));
    }

    @Override
    public void close() {
    }
}
