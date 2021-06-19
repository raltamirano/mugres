package mugres.core.utils;

import mugres.ipc.Listener;
import mugres.ipc.tcpip.MUGRESTCPIPClient;
import mugres.ipc.tcpip.MUGRESTCPIPServer;

import static mugres.ipc.tcpip.Utils.getMUGRESServerPortOrDefault;

public class IPCUtils {
    private IPCUtils() {}

    /**
     * Start a MUGRES TCP/IP server
     *
     * @param label short description of the name or purpose of this server (for logging and stuff like that)
     * @param args Command line arguments (for configuration)
     * @param clientMessageProcessor Listener for messages sent from clients to this server
     */
    public static void runMUGRESTCPIPServer(final String label,
                                            final String[] args,
                                            final Listener clientMessageProcessor) {
        try {
            final int port = getMUGRESServerPortOrDefault(System.getProperty("mugres.server.listener-port"));
            System.out.println(String.format("Starting MUGRES TCP/IP server %s on port %d ...", label, port));

            final MUGRESTCPIPServer mugresTCPIPServer = MUGRESTCPIPServer.of(port);
            mugresTCPIPServer.setListener(clientMessageProcessor);
            mugresTCPIPServer.start();
            System.out.println(String.format("MUGRES TCP/IP server %s started", label));

            Thread.currentThread().join();
            mugresTCPIPServer.stop();
        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * Start a MUGRES TCP/IP client
     *
     * @param args Command line arguments (for configuration)
     * @param clientMessageProcessor Listener for messages sent from clients to this node
     */
    public static void runMUGRESTCPIPClient(final String[] args,
                                            final Listener clientMessageProcessor) {
        try {
            final String host = args[0];
            final int port = Integer.parseInt(args[1]);
            System.out.println(String.format("Connecting to MUGRES TCP/IP server listening on %s:%d ...", host, port));

            final MUGRESTCPIPClient mugresTCPIPClient = MUGRESTCPIPClient.of(host, port);
            mugresTCPIPClient.setListener(clientMessageProcessor);
            mugresTCPIPClient.connect();
            System.out.println("MUGRES TCP/IP client connected");

            Thread.currentThread().join();
            mugresTCPIPClient.disconnect();
        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
