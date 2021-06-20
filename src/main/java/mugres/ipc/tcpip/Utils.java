package mugres.ipc.tcpip;

import static mugres.ipc.tcpip.MUGRESTCPIPServer.DEFAULT_PORT;

public class Utils {
    private Utils() {}

    public static int getMUGRESServerPortOrDefault(final String input) {
        try {
            if (input == null || input.trim().isEmpty())
                return DEFAULT_PORT;
            return Integer.parseInt(input);
        } catch (final Throwable ignore) {
            return DEFAULT_PORT;
        }
    }

    public static <X> mugres.ipc.Envelope<X> switchEnvelope(final aquelarre.Envelope<X> aquelarreMessage) {
        return mugres.ipc.Envelope.of(mugres.ipc.Header.of(aquelarreMessage.header().from(), aquelarreMessage.header().to()),
                aquelarreMessage.payload());
    }

    public static <X> aquelarre.Envelope<X> switchEnvelope(final mugres.ipc.Envelope<X> mugresMessage) {
        return aquelarre.Envelope.of(aquelarre.Header.of(mugresMessage.header().from(), mugresMessage.header().to()),
                mugresMessage.payload());
    }
}
