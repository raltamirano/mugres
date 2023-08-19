package mugres.tracker.performance.converters;

import mugres.tracker.Event;
import mugres.common.Instrument;
import mugres.common.Length;
import mugres.tracker.performance.Control;
import mugres.tracker.performance.Performance;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import static javax.sound.midi.Sequence.PPQ;
import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;
import static javax.sound.midi.ShortMessage.PROGRAM_CHANGE;
import static mugres.common.Length.PPQN;

public class ToMidiSequenceConverter implements Converter<Sequence> {
    private static final ToMidiSequenceConverter INSTANCE = new ToMidiSequenceConverter();

    private ToMidiSequenceConverter() {
    }

    public static ToMidiSequenceConverter getInstance() {
        return INSTANCE;
    }

    @Override
    public Sequence convert(final Performance performance) {
        try {
            final Sequence sequence = new Sequence(PPQ, PPQN);

            // Control track (tempo, key, time signature)
            final Track controlTrack = sequence.createTrack();
            trackName(controlTrack, CONTROL_TRACK);
            for(Control.ControlEvent controlEvent : performance.controlEvents())
                setControlParameters(controlTrack, controlEvent);

            for(mugres.tracker.performance.Track track : performance.tracks()) {
                final Track midiTrack = sequence.createTrack();
                trackName(midiTrack, track.track().name());
                programChange(midiTrack, track.channel(), track.instrument());
                for(Event event : track.events())
                    addNoteEvent(midiTrack, track.channel(), event);
            }

            setSequenceLength(sequence, performance.length());

            return sequence;
        } catch (final InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setSequenceLength(final Sequence sequence, final Length length) {
        setSequenceLength(sequence, length.length());
    }

    public static void setSequenceLength(final Sequence sequence, final long lengthInTicks) {
        sequence.getTracks()[0].get(sequence.getTracks()[0].size()-1).setTick(lengthInTicks);
    }

    private void setControlParameters(final Track controlTrack,
                                      final Control.ControlEvent controlEvent)
            throws InvalidMidiDataException {
        final long ticks = controlEvent.position().length();

        // Tempo
        int mpq = (60_000_000 / controlEvent.control().tempo());
        final MetaMessage tempoMessage = new MetaMessage();
        tempoMessage.setMessage(0x51, new byte[] {
                (byte)(mpq>>16 & 0xff),
                (byte)(mpq>>8 & 0xff),
                (byte)(mpq & 0xff)
        },3);
        final MidiEvent tempoEvent = new MidiEvent(tempoMessage, ticks);
        controlTrack.add(tempoEvent);

        // Time signature
        final int numerator = controlEvent.control().timeSignature().numerator();
        int notatedDenominator = controlEvent.control().timeSignature().denominator().denominator();
        int denominator = 0;
        while (notatedDenominator != 1) {
            notatedDenominator /= 2;
            denominator++;
        }

        final MetaMessage timeSignatureMessage = new MetaMessage();
        timeSignatureMessage.setMessage(0x58, new byte[] {
                (byte) numerator,
                (byte) denominator,
                (byte) PPQN,
                (byte) 8
        },4);
        final MidiEvent timeSignatureEvent = new MidiEvent(timeSignatureMessage, ticks);
        controlTrack.add(timeSignatureEvent);

        // TODO: Key
    }

    private void trackName(final Track midiTrack, final String name)
            throws InvalidMidiDataException {
        final MetaMessage metaMessage = new MetaMessage(0x03, name.getBytes(), name.length());
        midiTrack.add(new MidiEvent(metaMessage, 0L));
    }

    private void programChange(final Track midiTrack, final int channel, final Instrument instrument)
            throws InvalidMidiDataException {
        final MidiMessage midiMessage = new ShortMessage(PROGRAM_CHANGE, channel - 1, instrument.midi(), 0);
        midiTrack.add(new MidiEvent(midiMessage, 0L));
    }

    private void addNoteEvent(final Track midiTrack, final int channel, Event event)
            throws InvalidMidiDataException {
        long startTicks = event.position().length();

        final ShortMessage noteOn = new ShortMessage(NOTE_ON, channel - 1, event.pitch().midi(),
                event.velocity());
        midiTrack.add(new MidiEvent(noteOn, startTicks));
        final ShortMessage noteOff = new ShortMessage(NOTE_OFF, channel - 1, event.pitch().midi(),
                0);
        midiTrack.add(new MidiEvent(noteOff, startTicks + event.length()
                .length()));
    }

    private static final String CONTROL_TRACK = "Control Track";
}
