package mugres.ipc.tcpip;

import aquelarre.Server;
import mugres.ipc.protocol.Message;
import mugres.ipc.stream.DataInputStreamReader;
import mugres.ipc.stream.DataOutputStreamWriter;

import java.io.IOException;

public class MUGRESTCPIPServer {
    private final Server<Message> server;
    private final AquelarreMessageReaderAdapter messageReader;
    private final AquelarreMessageWriterAdapter messageWriter;

    private MUGRESTCPIPServer(final int port) {
        messageReader = new AquelarreMessageReaderAdapter(new DataInputStreamReader());
        messageWriter = new AquelarreMessageWriterAdapter(new DataOutputStreamWriter());
        server = aquelarre.Server.of(port, messageReader, messageWriter);
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

    public void broadcast(final Message message) {
        server.broadcast(message);
    }
    public static final int DEFAULT_PORT = 6477;
}
