package mugres.ipc.tcpip;

import aquelarre.Node;
import aquelarre.Server;
import mugres.ipc.protocol.Message;

import java.io.IOException;

public class MUGRESTCPIPServer extends MUGRESTCPIPNode {
    private final Server<Message> server;
    private MUGRESTCPIPServer(final int port) {
        super();

        server = aquelarre.Server.of(port, reader(), writer(), routingManager());
    }

    public static MUGRESTCPIPServer of() {
        return new MUGRESTCPIPServer(DEFAULT_PORT);
    }

    public static MUGRESTCPIPServer of(final int port) {
        return new MUGRESTCPIPServer(port);
    }

    public boolean isRunning() {
        return server.isRunning();
    }

    public void start() throws IOException {
        server.start();
    }

    public void stop() {
        server.stop();
    }

    private AquelarreRoutingManager routingManager() {
        return ROUTING_MANAGER;
    }

    @Override
    protected Node<Message> getAquelarreNode() {
        return server;
    }

    public static final int DEFAULT_PORT = 6477;
    private static final AquelarreRoutingManager ROUTING_MANAGER = new AquelarreRoutingManager();
}
