package mugres.ipc.stream.writers;

import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.MessageType;

import java.io.DataOutputStream;
import java.io.IOException;

public interface StreamMessageWriter<T extends Message> {
    void write(final T message, final DataOutputStream dataOutputStream) throws IOException;

    default void writeMessageType(final MessageType messageType, final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(messageType.identifier());
    }
}
