package mugres.ipc.protocol.messages;

import mugres.common.Instrument;
import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.MessageType;

public class SetTrackMessage extends Message {
    private final Instrument instrument;

    private SetTrackMessage(final Instrument instrument) {
        super(MessageType.SET_TRACK);

        if (instrument == null)
            throw new IllegalArgumentException("instrument");

        this.instrument = instrument;
    }

    public static SetTrackMessage of(final Instrument instrument) {
        return new SetTrackMessage(instrument);
    }

    public Instrument instrument() {
        return instrument;
    }

    @Override
    public String toString() {
        return super.toString() + " - Instrument: " + instrument;
    }
}
