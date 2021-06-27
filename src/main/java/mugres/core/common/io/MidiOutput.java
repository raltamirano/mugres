package mugres.core.common.io;

import mugres.core.common.InstrumentChange;
import mugres.core.common.Signal;
import mugres.core.notation.Song;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;

import static javax.sound.midi.ShortMessage.NOTE_ON;
import static javax.sound.midi.ShortMessage.PROGRAM_CHANGE;
import static mugres.core.common.MIDI.END_OF_TRACK;

public class MidiOutput implements Output {
    private final Receiver midiOutputPort;

    private MidiOutput(final Receiver midiOutputPort) {
        this.midiOutputPort = midiOutputPort;
    }

    public static Output of(final Receiver midiOutputPort) {
        return new MidiOutput(midiOutputPort);
    }

    @Override
    public void send(final Signal signal) {
        try {
            final ShortMessage message = new ShortMessage(NOTE_ON, signal.getChannel(),
                    signal.getPlayed().pitch().getMidi(),
                    signal.isActive() ? signal.getPlayed().velocity() : 0);
            midiOutputPort.send(message, -1);
        } catch (final InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(final InstrumentChange instrumentChange) {
        try {
            final ShortMessage message = new ShortMessage(PROGRAM_CHANGE, instrumentChange.channel(),
                    instrumentChange.instrument().getMidi(), 0);
            midiOutputPort.send(message, -1);
        } catch (final InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(final Song song) {
        if (song == null)
            throw new IllegalArgumentException("song");

        try {
            final Sequencer sequencer = MidiSystem.getSequencer(false);
            sequencer.setSequence(song.toMidiSequence());
            sequencer.open();
            sequencer.getTransmitter().setReceiver(midiOutputPort);
            sequencer.addMetaEventListener(new CloseSequencerOnSequenceEnd(sequencer));
            sequencer.start();
        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public Receiver getMidiOutputPort() {
        return midiOutputPort;
    }

    private class CloseSequencerOnSequenceEnd implements MetaEventListener {
        private final Sequencer sequencer;

        public CloseSequencerOnSequenceEnd(Sequencer sequencer) {
            this.sequencer = sequencer;
        }

        @Override
        public void meta(final MetaMessage meta) {
            if (meta.getType() == END_OF_TRACK)
                sequencer.close();
        }
    }
}
