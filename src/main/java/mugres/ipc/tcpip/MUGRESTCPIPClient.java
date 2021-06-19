package mugres.ipc.tcpip;

import aquelarre.Client;
import aquelarre.Node;
import mugres.ipc.protocol.Message;

import java.io.IOException;

import static mugres.ipc.tcpip.MUGRESTCPIPServer.DEFAULT_PORT;

public class MUGRESTCPIPClient extends MUGRESTCPIPNode {
    private Client<Message> client;

    private MUGRESTCPIPClient(final String host, final int port) {
        super();

        client = Client.of(host, port, reader(), writer());
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

    @Override
    protected Node<Message> getAquelarreNode() {
        return client;
    }
}
