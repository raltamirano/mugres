package mugres.ipc.tcpip;

import aquelarre.Envelope;
import aquelarre.RoutingManager;
import mugres.ipc.protocol.Message;

public class AquelarreRoutingManager implements RoutingManager<Message> {
    @Override
    public boolean isValidRoute(final Envelope<Message> envelope) {
        final boolean toServerOnlyCheck = !envelope.payload().type().toServerOnly() || envelope.wasSentToServer();
        final boolean fromServerOnlyCheck = !envelope.payload().type().fromServerOnly() || envelope.wasSentFromServer();
        return toServerOnlyCheck && fromServerOnlyCheck;
    }
}
