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
}
