package mugres.ipc.protocol.messages;

import mugres.core.common.Signals;
import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.MessageType;

public class SignalsMessage extends Message {
    private final Signals signals;

    public SignalsMessage(final Signals signals) {
        super(MessageType.SIGNALS);

        if (signals == null)
            throw new IllegalArgumentException("signals");

        this.signals = signals;
    }

    public Signals signals() {
        return signals;
    }

    @Override
    public String toString() {
        return super.toString() + " - " + signals.toString();
    }
}
