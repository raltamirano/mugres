package mugres.ipc.tcpip;

import aquelarre.Envelope;
import aquelarre.MessageListener;
import mugres.ipc.Listener;
import mugres.ipc.protocol.Message;

import static mugres.ipc.tcpip.Utils.switchEnvelope;

public class AquelarreMessageListenerAdapter implements MessageListener<Message> {
    private final Listener listener;

    public AquelarreMessageListenerAdapter(final Listener listener) {
        if (listener == null)
            throw new IllegalArgumentException("listener");

        this.listener = listener;
    }

    @Override
    public void onMessage(final Envelope<Message> message) {
        listener.onMessage(switchEnvelope(message));
    }
}
