package mugres.ipc.tcpip;

import aquelarre.Client;
import mugres.ipc.protocol.Message;
import mugres.ipc.stream.DataInputStreamReader;
import mugres.ipc.stream.DataOutputStreamWriter;

import java.io.IOException;

import static mugres.ipc.tcpip.MUGRESTCPIPServer.DEFAULT_PORT;

public class MUGRESTCPIPClient {
    private Client<Message> client;
    private final AquelarreMessageReaderAdapter messageReader;
    private final AquelarreMessageWriterAdapter messageWriter;

    private MUGRESTCPIPClient(final String host, final int port) {
        messageReader = new AquelarreMessageReaderAdapter(new DataInputStreamReader());
        messageWriter = new AquelarreMessageWriterAdapter(new DataOutputStreamWriter());
        client = Client.of(host, port, messageReader, messageWriter);
    }

    public static MUGRESTCPIPClient of(final String host) {
        return of(host, DEFAULT_PORT);
    }

    public static MUGRESTCPIPClient of(final String host, final int port) {
        return new MUGRESTCPIPClient(host, port);
    }

    public String host() {
        return client.host();
    }

    public int port() {
        return client.port();
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public synchronized void connect() throws IOException {
        client.connect();
    }

    public synchronized void disconnect() throws IOException {
        client.disconnect();
    }

    public void broadcast(final Message message) throws IOException {
        client.broadcast(message);
    }
}
