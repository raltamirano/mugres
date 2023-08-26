package mugres.tracker;

import mugres.common.DataType;
import mugres.common.Instrument;
import mugres.common.TrackReference;
import mugres.parametrizable.Parameter;
import mugres.parametrizable.ParametrizableSupport;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static mugres.common.MIDI.DEFAULT_CHANNEL;
import static mugres.common.MIDI.MAX_CHANNEL;
import static mugres.common.MIDI.MIN_CHANNEL;

public class Track extends TrackerElement {
    private TrackReference reference;
    private Instrument instrument;
    private int channel;

    private static final Set<Parameter> PARAMETERS;

    static {
        PARAMETERS = new HashSet<>();

        PARAMETERS.add(Parameter.of("name", "Name", 1, "Name",
                DataType.TEXT, false, ""));
        PARAMETERS.add(Parameter.of("instrument", "Instrument", 2, "Instrument",
                DataType.INSTRUMENT, false,120));
        PARAMETERS.add(Parameter.of("channel", "MIDI Channel", 3, "MIDI Channel",
                DataType.INTEGER, false, DEFAULT_CHANNEL, MIN_CHANNEL, MAX_CHANNEL, false));
    }

    private Track(final UUID id, final String name, final Instrument instrument, final int channel) {
        super(id, name,null);

        this.instrument = instrument;
        this.channel = channel;
        this.reference = TrackReference.of(id);
    }

    public TrackReference reference() {
        return reference;
    }

    @Override
    protected ParametrizableSupport createParametrizableSupport() {
        return ParametrizableSupport.forTarget(PARAMETERS, this);
    }

    public static Track of(final UUID id, final String name, final Instrument instrument) {
        return new Track(id, name, instrument, DEFAULT_CHANNEL);
    }

    public static Track of(final UUID id, final String name, final Instrument instrument, final int channel) {
        return new Track(id, name, instrument, channel);
    }

    public Instrument instrument() {
        return instrument;
    }

    public void instrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public int channel() {
        return channel;
    }

    public void channel(int channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id='" + id() + '\'' +
                ", name='" + name() + '\'' +
                ", instrument=" + instrument +
                ", channel=" + channel +
                '}';
    }

    // A handful of commonly used tracks.
    public enum WellKnownTracks {
        DRUMS(new Track(UUID.fromString("ee08828e-3181-40cf-ac34-61789f6155f0"),
                "Drums", Instrument.DrumKit, 9)),
        BASS(new Track(UUID.fromString("2b1054f0-53a4-4f6a-9e31-6328f4d04d3c"),
                "Bass", Instrument.Electric_Bass_Finger, 0)),
        GUITAR1(new Track(UUID.fromString("07e6cbbc-a17e-4c4e-b258-8f644fd10da6"),
                "Guitar I", Instrument.Overdriven_Guitar, 1)),
        GUITAR2(new Track(UUID.fromString("90418655-f66c-473b-9bdc-6bf44ad9e1fa"),
                "Guitar II", Instrument.Overdriven_Guitar, 2)),
        GUITAR3(new Track(UUID.fromString("ae6b958e-8e3a-475d-b3ab-881e8549dc9a"),
                "Guitar III", Instrument.Overdriven_Guitar, 3)),
        GUITAR4(new Track(UUID.fromString("5c025ad7-a106-42f0-8aad-055463abed84"),
                "Guitar IV", Instrument.Overdriven_Guitar, 4)),
        VOX1(new Track(UUID.fromString("c31fd68c-e89c-48fe-858e-82537795bbe3"),
                "Vox I", Instrument.Synth_Voice, 5)),
        VOX2(new Track(UUID.fromString("5236e225-583b-48ce-a2df-5a84976b7898"),
                "Vox II", Instrument.Synth_Voice, 6)),
        PIANO(new Track(UUID.fromString("4ca0f932-e5d9-4a5f-a9c2-fca351c2b8dc"),
                "Piano", Instrument.Acoustic_Grand_Piano, 7)),
        ORGAN(new Track(UUID.fromString("295b37ee-37d3-40dc-8f0c-4a17eea27031"),
                "Organ", Instrument.Hammond_Organ, 8)),
        STRINGS1(new Track(UUID.fromString("25f7bb6f-5f54-4e4f-a8ae-75917be0b2dd"),
                "Synth Strings I", Instrument.String_Ensemble_1, 10)),
        STRINGS2(new Track(UUID.fromString("3a3ab054-f356-44d2-a035-367676fa4a3f"),
                "Synth Strings II", Instrument.String_Ensemble_2, 11)),
        PAD1(new Track(UUID.fromString("bd29063d-3420-4e2b-900e-7533f70da267"),
                "Pad I", Instrument.Synth_Pad_1_NewAge, 12)),
        PAD2(new Track(UUID.fromString("e0f26a3a-6068-4f6b-939d-72c6f256148b"),
                "Pad II", Instrument.Synth_Pad_2_Warm, 13)),
        CHOIR1(new Track(UUID.fromString("955fc4f0-a917-4944-87b5-0a60f55cf9fe"),
                "Choir I", Instrument.Voice_Oohs,  14)),
        CHOIR2(new Track(UUID.fromString("4c4b4c8b-51c7-4263-b5cf-0f16feee9ba1"),
                "Choir II", Instrument.Choir_Aahs, 15));
        
        private Track track;

        WellKnownTracks(final Track track) {
            this.track = track;
        }

        public Track track() {
            return track;
        }
    }
}
