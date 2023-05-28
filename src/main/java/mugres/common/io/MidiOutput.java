package mugres.common.io;

import mugres.common.ControlChange;
import mugres.common.InstrumentChange;
import mugres.live.Signal;
import mugres.tracker.Song;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;

import static javax.sound.midi.ShortMessage.CONTROL_CHANGE;
import static javax.sound.midi.ShortMessage.NOTE_ON;
import static javax.sound.midi.ShortMessage.PROGRAM_CHANGE;
import static mugres.common.MIDI.END_OF_TRACK;

public class MidiOutput extends Output {
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
            if (filters().isEmpty()) {
                sendSignal(signal);
            } else {
                for(Signal s : IOHelper.applyFilters(signal, filters()).signals())
                    sendSignal(s);
            }
        } catch (final InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(final InstrumentChange instrumentChange) {
        try {
            final ShortMessage message = new ShortMessage(PROGRAM_CHANGE, instrumentChange.channel() - 1,
                    instrumentChange.instrument().midi(), 0);
            midiOutputPort.send(message, -1);
        } catch (final InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(final ControlChange controlChange) {
        if (!(controlChange instanceof ControlChange.MidiControlChange))
            throw new RuntimeException(getClass().getSimpleName() + "only supports " +
                    ControlChange.MidiControlChange.class.getSimpleName() + " Control Changes!");
        try {
            final ShortMessage message = new ShortMessage(CONTROL_CHANGE, controlChange.channel() - 1,
                    controlChange.controller(), (int)controlChange.value());
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

    private void sendSignal(final Signal signal) throws InvalidMidiDataException {
        final ShortMessage message = new ShortMessage(NOTE_ON, signal.channel() - 1,
                signal.pitch().midi(), signal.velocity());
        midiOutputPort.send(message, -1);
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
