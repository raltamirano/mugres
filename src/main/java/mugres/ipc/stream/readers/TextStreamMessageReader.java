package mugres.ipc.stream.readers;

import mugres.ipc.protocol.MessageType;
import mugres.ipc.protocol.messages.TextMessage;

import java.io.DataInputStream;
import java.io.IOException;

public class TextStreamMessageReader implements StreamMessageReader<TextMessage> {
    @Override
    public TextMessage read(final MessageType messageType, final DataInputStream dataInputStream) throws IOException {
        return new TextMessage(dataInputStream.readUTF());
    }
}
