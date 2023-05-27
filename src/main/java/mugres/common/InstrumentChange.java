package mugres.common;

/**
 * Instrument change signal
 */
public class InstrumentChange {
    private final int channel;
    private final Instrument instrument;

    private InstrumentChange(final int channel, final Instrument instrument) {
        if (instrument == null)
            throw new IllegalArgumentException("instrument");

        this.channel = channel;
        this.instrument = instrument;
    }

    public static InstrumentChange of(final int channel, final Instrument instrument) {
        return new InstrumentChange(channel, instrument);
    }

    public int channel() {
        return channel;
    }

    public Instrument instrument() {
        return instrument;
    }
}
