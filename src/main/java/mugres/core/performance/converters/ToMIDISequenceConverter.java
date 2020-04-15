package mugres.core.performance.converters;

import mugres.core.common.Event;
import mugres.core.performance.Control;
import mugres.core.performance.Performance;

import javax.sound.midi.*;

import static javax.sound.midi.Sequence.PPQ;
import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;

public class ToMIDISequenceConverter implements Converter<Sequence> {
    @Override
    public Sequence convert(final Performance performance) {
        try {
            final Sequence sequence = new Sequence(PPQ, PPQ_RESOLUTION);

            // Control track (tempo, key, time signature)
            final Track controlTrack = sequence.createTrack();
            setTrackName(controlTrack, CONTROL_TRACK);
            for(Control.ControlEvent controlEvent : performance.getControlEvents())
                setControlParameters(controlTrack, controlEvent);

            for(mugres.core.performance.Track track : performance.getTracks()) {
                final Track midiTrack = sequence.createTrack();
                setTrackName(midiTrack, track.getParty());
                for(Event event : track.getEvents())
                    addNoteEvents(midiTrack, track.getChannel(), event);
            }

            setSequenceLength(sequence, performance.getLength().toPPQTicks(PPQ_RESOLUTION));

            return sequence;
        } catch (final InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setSequenceLength(final Sequence sequence, final long lengthInTicks) {
        sequence.getTracks()[0].get(sequence.getTracks()[0].size()-1).setTick(lengthInTicks);
    }

    private void setControlParameters(final Track controlTrack,
                                      final Control.ControlEvent controlEvent)
            throws InvalidMidiDataException {
        final long ticks = controlEvent.getPosition().toPPQTicks(PPQ_RESOLUTION);

        // Tempo
        int mpq = (int)(60_000_000 / controlEvent.getControl().getTempo());
        final MetaMessage tempoMessage = new MetaMessage();
        tempoMessage.setMessage(0x51, new byte[] {
                (byte)(mpq>>16 & 0xff),
                (byte)(mpq>>8 & 0xff),
                (byte)(mpq & 0xff)
        },3);
        final MidiEvent tempoEvent = new MidiEvent(tempoMessage, ticks);
        controlTrack.add(tempoEvent);

        // Time signature
        final int numerator = controlEvent.getControl().getTimeSignature().getNumerator();
        int notatedDenominator = controlEvent.getControl().getTimeSignature().getDenominator().denominator();
        int denominator = 0;
        while (notatedDenominator != 1) {
            notatedDenominator /= 2;
            denominator++;
        }

        final MetaMessage timeSignatureMessage = new MetaMessage();
        timeSignatureMessage.setMessage(0x58, new byte[] {
                (byte) numerator,
                (byte) denominator,
                (byte) PPQ_RESOLUTION,
                (byte) 8 },4);
        final MidiEvent timeSignatureEvent = new MidiEvent(timeSignatureMessage, ticks);
        controlTrack.add(timeSignatureEvent);

        // TODO: Key
    }

    private void setTrackName(final Track midiTrack, final String name)
            throws InvalidMidiDataException {
        final MetaMessage metaMessage = new MetaMessage(0x03, name.getBytes(), name.length());
        midiTrack.add(new MidiEvent(metaMessage, (long) 0));
    }

    private void addNoteEvents(final Track midiTrack, final int channel, Event event)
            throws InvalidMidiDataException {
        long startTicks = event.getPosition().toPPQTicks(PPQ_RESOLUTION);

        final ShortMessage noteOn = new ShortMessage(NOTE_ON, channel, event.getPlayed().getPitch().getMidi(),
                event.getPlayed().getVelocity());
        midiTrack.add(new MidiEvent(noteOn, startTicks));
        final ShortMessage noteOff = new ShortMessage(NOTE_OFF, channel, event.getPlayed().getPitch().getMidi(),
                0);
        midiTrack.add(new MidiEvent(noteOff, startTicks + event.getPlayed().getValue().length()
                .toPPQTicks(PPQ_RESOLUTION)));
    }

    private static final int PPQ_RESOLUTION = 480;
    private static final String CONTROL_TRACK = "Control Track";
}
