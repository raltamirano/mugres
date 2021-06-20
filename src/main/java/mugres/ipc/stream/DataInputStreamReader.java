package mugres.ipc.stream;

import mugres.ipc.Reader;
import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.MessageType;
import mugres.ipc.stream.readers.PlainStreamMessageReader;
import mugres.ipc.stream.readers.SignalsStreamMessageReader;
import mugres.ipc.stream.readers.StreamMessageReader;
import mugres.ipc.stream.readers.TextStreamMessageReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataInputStreamReader implements Reader {
    private final Map<MessageType, StreamMessageReader> READERS = new HashMap<>();

    public DataInputStreamReader() {
        configureReaders();
    }

    @Override
    public Message read(final DataInputStream dataInputStream) throws IOException {
        if (dataInputStream == null)
            throw new IllegalArgumentException("dataInputStream");

        final int read = dataInputStream.readInt();
        final MessageType messageType = MessageType.forIdentifier(read);
        final StreamMessageReader streamMessageReader = READERS.get(messageType);
        if (streamMessageReader == null)
            throw new RuntimeException(String.format("Internal error: No reader for Message Type '%s'", messageType));

        return streamMessageReader.read(messageType, dataInputStream);
    }

    private void configureReaders() {
        READERS.put(MessageType.SIGNALS, new SignalsStreamMessageReader());
        READERS.put(MessageType.BYE, new PlainStreamMessageReader());
        READERS.put(MessageType.TEXT, new TextStreamMessageReader());
    }
}
