package mugres.core.common;

public enum DrumKit {
    ABD(35, "Acoustic Bass Drum"),
    BD(36, "Bass Drum"),
    SS(37, "Side Stick"),
    SD(38, "Acoustic Snare"),
    HC(39, "Hand Clap"),
    ES(40, "Electric Snare"),
    LFT(41, "Low Floor Tom"),
    CHH(42, "Closed Hi Hat"),
    HFT(43, "High Floor Tom"),
    PHH(44, "Pedal Hi Hat"),
    LT(45, "Low Tom"),
    OHH(46, "Open hi Hat"),
    LMT(47, "Low Mid Tom"),
    HMT(48, "Hi Mid Tom"),
    CR1(49, "Crash Cymbal 1"),
    HT(50, "High Tom"),
    RD1(51, "Ride Cymbal 1"),
    CH(52, "Chinese Cymbal"),
    RB(53, "Ride Bell"),
    TA(54, "Tambourine"),
    SP(55, "Splash Cymbal"),
    CB(56, "Cowbell"),
    CR2(57, "Crash Cymbal 2"),
    VS(58, "Vibra Slap"),
    RD2(59, "Ride Cymbal 2"),
    HB(60, "Hi Bongo"),
    LB(61, "Low Bongo"),
    MHC(62, "Mute Hi Conga"),
    OHC(63, "Open Hi Conga"),
    LC(64, "Low Conga"),
    HTI(65, "High Timbale"),
    LTI(66, "Low Timbale"),
    HA(67, "High Agogo"),
    LA(68, "Low Agogo"),
    CA(69, "Cabasa"),
    MA(70, "Maracas"),
    SW(71, "Short Whistle"),
    LW(72, "Long Whistle"),
    SG(73, "Short Guiro"),
    LG(74, "Long Guiro"),
    CL(75, "Claves"),
    HWB(76, "Hi Wood Block"),
    LWB(77, "Low Wood Block"),
    MCU(78, "Mute Cuica"),
    OCU(79, "Open Cuica"),
    MTR(80, "Mute Triangle"),
    OTR(81, "Open Triangle");

    private int midi;
    private String label;

    DrumKit(final int midi, final String label) {
        this.midi = midi;
        this.label = label;
    }

    public int midi() {
        return midi;
    }

    public String label() {
        return label;
    }

    public Pitch pitch() {
        return Pitch.of(midi);
    }
}
