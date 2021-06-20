package mugres.ipc;

import mugres.ipc.protocol.Message;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Writer {
    void write(final Envelope<Message> message, final DataOutputStream dataOutputStream) throws IOException;
}
