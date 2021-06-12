package mugres.ipc.stream.readers;

import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.MessageType;

import java.io.DataInputStream;

public class PlainStreamMessageReader implements StreamMessageReader<Message> {
    @Override
    public Message read(final MessageType messageType, final DataInputStream dataInputStream) {
        return new Message(messageType);
    }
}
