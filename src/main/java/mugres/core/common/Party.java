package mugres.core.common;

import java.util.Objects;

public class Party {
    private String name;
    private Instrument instrument;
    private int channel;

    public Party(String name, Instrument instrument, int channel) {
        this.name = name;
        this.instrument = instrument;
        this.channel = channel;
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
        Party party = (Party) o;
        return name.equals(party.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Party{" +
                "name='" + name + '\'' +
                ", instrument=" + instrument +
                ", channel=" + channel +
                '}';
    }

    // A handful of commonly used parties.
    public enum WellKnownParties {
        DRUMS(new Party("Drums", Instrument.DrumKit, 9)),
        BASS(new Party("Bass", Instrument.Electric_Bass_Finger, 0)),
        GUITAR1(new Party("Guitar I", Instrument.Overdriven_Guitar, 1)),
        GUITAR2(new Party("Guitar II", Instrument.Overdriven_Guitar, 2)),
        GUITAR3(new Party("Guitar III", Instrument.Overdriven_Guitar, 3)),
        GUITAR4(new Party("Guitar IV", Instrument.Overdriven_Guitar, 4)),
        VOX1(new Party("Vox I", Instrument.Synth_Voice, 5)),
        VOX2(new Party("Vox II", Instrument.Synth_Voice, 6)),
        PIANO(new Party("Piano", Instrument.Acoustic_Grand_Piano, 7)),
        ORGAN(new Party("Organ", Instrument.Hammond_Organ, 8)),
        STRINGS1(new Party("Synth Strings I", Instrument.String_Ensemble_1, 10)),
        STRINGS2(new Party("Synth Strings II", Instrument.String_Ensemble_2, 11)),
        PAD1(new Party("Pad I", Instrument.Synth_Pad_1_NewAge, 12)),
        PAD2(new Party("Pad II", Instrument.Synth_Pad_2_Warm, 13)),
        CHOIR1(new Party("Choir I", Instrument.Voice_Oohs,  14)),
        CHOIR2(new Party("Choir II", Instrument.Choir_Aahs, 15));
        
        private Party party;

        WellKnownParties(final Party party) {
            this.party = party;
        }

        public Party party() {
            return party;
        }
    }
}
