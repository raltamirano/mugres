package mugres.ipc.protocol.messages;

import mugres.common.Signals;
import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.MessageType;

public class SignalsMessage extends Message {
    private final Signals signals;

    private SignalsMessage(final Signals signals) {
        super(MessageType.SIGNALS);

        if (signals == null)
            throw new IllegalArgumentException("signals");

        this.signals = signals;
    }

    public static SignalsMessage of(final Signals signals) {
        return new SignalsMessage(signals);
    }

    public Signals signals() {
        return signals;
    }

    @Override
    public String toString() {
        return super.toString() + " - " + signals.toString();
    }
}
