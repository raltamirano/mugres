package mugres.ipc;

import mugres.ipc.protocol.Message;

public interface Listener {
    void onMessage(final Message message);
}
