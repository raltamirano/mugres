package mugres.tracker;

import mugres.common.Instrument;

import java.util.Objects;

import static mugres.common.MIDI.DEFAULT_CHANNEL;

public class Track implements Comparable<Track> {
    private String name;
    private Instrument instrument;
    private int channel;

    private Track(final String name, final Instrument instrument, final int channel) {
        this.name = name;
        this.instrument = instrument;
        this.channel = channel;
    }

    public static Track of(final String name, final Instrument instrument) {
        return new Track(name, instrument, DEFAULT_CHANNEL);
    }

    public static Track of(final String name, final Instrument instrument, final int channel) {
        return new Track(name, instrument, channel);
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return name.equals(track.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Track{" +
                "name='" + name + '\'' +
                ", instrument=" + instrument +
                ", channel=" + channel +
                '}';
    }

    @Override
    public int compareTo(final Track o) {
        return name.compareTo(o.name);
    }

    // A handful of commonly used tracks.
    public enum WellKnownTracks {
        DRUMS(new Track("Drums", Instrument.DrumKit, 9)),
        BASS(new Track("Bass", Instrument.Electric_Bass_Finger, 0)),
        GUITAR1(new Track("Guitar I", Instrument.Overdriven_Guitar, 1)),
        GUITAR2(new Track("Guitar II", Instrument.Overdriven_Guitar, 2)),
        GUITAR3(new Track("Guitar III", Instrument.Overdriven_Guitar, 3)),
        GUITAR4(new Track("Guitar IV", Instrument.Overdriven_Guitar, 4)),
        VOX1(new Track("Vox I", Instrument.Synth_Voice, 5)),
        VOX2(new Track("Vox II", Instrument.Synth_Voice, 6)),
        PIANO(new Track("Piano", Instrument.Acoustic_Grand_Piano, 7)),
        ORGAN(new Track("Organ", Instrument.Hammond_Organ, 8)),
        STRINGS1(new Track("Synth Strings I", Instrument.String_Ensemble_1, 10)),
        STRINGS2(new Track("Synth Strings II", Instrument.String_Ensemble_2, 11)),
        PAD1(new Track("Pad I", Instrument.Synth_Pad_1_NewAge, 12)),
        PAD2(new Track("Pad II", Instrument.Synth_Pad_2_Warm, 13)),
        CHOIR1(new Track("Choir I", Instrument.Voice_Oohs,  14)),
        CHOIR2(new Track("Choir II", Instrument.Choir_Aahs, 15));
        
        private Track track;

        WellKnownTracks(final Track track) {
            this.track = track;
        }

        public Track track() {
            return track;
        }
    }
}
