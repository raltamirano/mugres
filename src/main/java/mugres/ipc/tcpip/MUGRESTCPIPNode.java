package mugres.ipc.tcpip;

import aquelarre.Node;
import mugres.ipc.Listener;
import mugres.ipc.protocol.Message;
import mugres.ipc.stream.DataInputStreamReader;
import mugres.ipc.stream.DataOutputStreamWriter;

import java.io.IOException;
import java.util.UUID;

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

    public UUID nodeId() {
        return getAquelarreNode().nodeId();
    }

    public void broadcast(final Message message) throws IOException {
        getAquelarreNode().broadcast(message);
    }

    protected abstract Node<Message> getAquelarreNode();
}
