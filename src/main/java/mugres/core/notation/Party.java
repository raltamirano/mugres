package mugres.core.notation;

import mugres.core.common.Instrument;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
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

    // A handful of commonly used parties.
    public static final Party DRUMS = new Party("Drums", Instrument.DrumKit, 9);
    public static final Party BASS = new Party("Bass", Instrument.Electric_Bass_Finger, 0);
    public static final Party GUITAR1 = new Party("Guitar I", Instrument.Overdriven_Guitar, 1);
    public static final Party GUITAR2 = new Party("Guitar II", Instrument.Overdriven_Guitar, 2);
    public static final Party GUITAR3 = new Party("Guitar III", Instrument.Overdriven_Guitar, 3);
    public static final Party GUITAR4 = new Party("Guitar IV", Instrument.Overdriven_Guitar, 4);
    public static final Party VOX1 = new Party("Vox I", Instrument.Synth_Voice, 5);
    public static final Party VOX2 = new Party("Vox II", Instrument.Synth_Voice, 6);
    public static final Party VOX3 = new Party("Vox III", Instrument.Synth_Voice, 7);
    public static final Party VOX4 = new Party("Vox IV", Instrument.Synth_Voice, 8);
    public static final Party STRINGS1 = new Party("Synth Strings I", Instrument.String_Ensemble_1, 10);
    public static final Party STRINGS2 = new Party("Synth Strings II", Instrument.String_Ensemble_2, 11);
    public static final Party PAD1 = new Party("Pad I", Instrument.Synth_Pad_1_NewAge, 12);
    public static final Party PAD2 = new Party("Pad II", Instrument.Synth_Pad_2_Warm, 13);
    public static final Party CHOIR1 = new Party("Choir I", Instrument.Voice_Oohs,  14);
    public static final Party CHOIR2 = new Party("Choir II", Instrument.Choir_Aahs, 15);   
}
