package mugres.ipc.tcpip;

import aquelarre.MessageListener;
import aquelarre.MessageReader;
import mugres.ipc.Listener;
import mugres.ipc.protocol.Message;
import mugres.ipc.stream.DataInputStreamReader;

import java.io.DataInputStream;
import java.io.IOException;

public class AquelarreMessageListenerAdapter implements MessageListener<Message> {
    private final Listener listener;

    public AquelarreMessageListenerAdapter(final Listener listener) {
        if (listener == null)
            throw new IllegalArgumentException("listener");

        this.listener = listener;
    }

    @Override
    public void onMessage(final Message message) {
        listener.onMessage(message);
    }
}
