package mugres.ipc.protocol.messages;

import mugres.core.common.Instrument;
import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.MessageType;

public class SetPartyMessage extends Message {
    private final Instrument instrument;

    private SetPartyMessage(final Instrument instrument) {
        super(MessageType.SET_PARTY);

        if (instrument == null)
            throw new IllegalArgumentException("instrument");

        this.instrument = instrument;
    }

    public static SetPartyMessage of(final Instrument instrument) {
        return new SetPartyMessage(instrument);
    }

    public Instrument instrument() {
        return instrument;
    }

    @Override
    public String toString() {
        return super.toString() + " - Instrument: " + instrument;
    }
}
