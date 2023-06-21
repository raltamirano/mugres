package mugres.ipc.stream;

import mugres.ipc.Envelope;
import mugres.ipc.Header;
import mugres.ipc.Writer;
import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.MessageType;
import mugres.ipc.stream.writers.TrackListStreamMessageWriter;
import mugres.ipc.stream.writers.PlainStreamMessageWriter;
import mugres.ipc.stream.writers.SetTrackStreamMessageWriter;
import mugres.ipc.stream.writers.SignalsStreamMessageWriter;
import mugres.ipc.stream.writers.StreamMessageWriter;
import mugres.ipc.stream.writers.TextStreamMessageWriter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataOutputStreamWriter implements Writer {
    private final Map<MessageType, StreamMessageWriter> WRITERS = new HashMap<>();

    public DataOutputStreamWriter() {
        configureWriters();
    }

    @Override
    public void write(final Envelope<Message> message, final DataOutputStream dataOutputStream) throws IOException {
        if (message == null)
            throw new IllegalArgumentException("message");
        if (dataOutputStream == null)
            throw new IllegalArgumentException("dataOutputStream");

        writeEnvelopeHeader(message.header(), dataOutputStream);

        final StreamMessageWriter writer = WRITERS.get(message.payload().type());
        if (writer == null)
            throw new RuntimeException(String.format("Internal error: No writer for Message Type '%s'",
                    message.payload().type()));

        writer.write(message.payload(), dataOutputStream);
    }

    private void writeEnvelopeHeader(final Header header, final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeUTF(header.from());
        dataOutputStream.writeUTF(header.to());
    }

    private void configureWriters() {
        WRITERS.put(MessageType.SIGNALS, new SignalsStreamMessageWriter());
        WRITERS.put(MessageType.BYE, new PlainStreamMessageWriter());
        WRITERS.put(MessageType.TEXT, new TextStreamMessageWriter());
        WRITERS.put(MessageType.SET_TRACK, new SetTrackStreamMessageWriter());
        WRITERS.put(MessageType.TRACK_LIST, new TrackListStreamMessageWriter());
    }
}
