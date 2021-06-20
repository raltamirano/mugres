package mugres.ipc.tcpip;

import aquelarre.Node;
import mugres.ipc.Listener;
import mugres.ipc.protocol.Message;
import mugres.ipc.stream.DataInputStreamReader;
import mugres.ipc.stream.DataOutputStreamWriter;

import java.io.IOException;

public abstract class MUGRESTCPIPNode {
    private Listener listener;
    private AquelarreMessageListenerAdapter listenerAdapter;
    private final AquelarreMessageReaderAdapter messageReader;
    private final AquelarreMessageWriterAdapter messageWriter;

    protected MUGRESTCPIPNode() {
        messageReader = new AquelarreMessageReaderAdapter(new DataInputStreamReader());
        messageWriter = new AquelarreMessageWriterAdapter(new DataOutputStreamWriter());
    }

    public AquelarreMessageReaderAdapter reader() {
        return messageReader;
    }

    public AquelarreMessageWriterAdapter writer() {
        return messageWriter;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(final Listener listener) {
        this.listener = listener;

        if (listener == null)
            listenerAdapter = null;
        else
            listenerAdapter = new AquelarreMessageListenerAdapter(listener);

        getAquelarreNode().setMessageListener(listenerAdapter);
    }

    public void send(final String to, final Message message) throws IOException {
        getAquelarreNode().send(to, message);
    }

    /** Send a message targeted at the MUGRES server */
    public void sendToServer(final Message message) throws IOException {
        send(Node.SERVER, message);
    }

    public void broadcast(final Message message) throws IOException {
        getAquelarreNode().broadcast(message);
    }

    protected abstract Node<Message> getAquelarreNode();
}
