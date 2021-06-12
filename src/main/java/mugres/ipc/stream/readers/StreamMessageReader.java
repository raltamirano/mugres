package mugres.ipc.stream.readers;

import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.MessageType;

import java.io.DataInputStream;
import java.io.IOException;

public interface StreamMessageReader<T extends Message> {
    T read(final MessageType messageType, final DataInputStream dataInputStream) throws IOException;
}
