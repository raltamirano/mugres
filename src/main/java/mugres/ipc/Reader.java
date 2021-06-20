package mugres.ipc;

import mugres.ipc.protocol.Message;

import java.io.DataInputStream;
import java.io.IOException;

public interface Reader {
    Envelope<Message> read(final DataInputStream dataInputStream) throws IOException;
}
