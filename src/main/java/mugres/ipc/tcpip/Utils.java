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
}
