package mugres.ipc.stream;

import mugres.ipc.Writer;
import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.MessageType;
import mugres.ipc.stream.writers.PlainStreamMessageWriter;
import mugres.ipc.stream.writers.SignalsStreamMessageWriter;
import mugres.ipc.stream.writers.StreamMessageWriter;

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
    public void write(final Message message, final DataOutputStream dataOutputStream) throws IOException {
        if (message == null)
            throw new IllegalArgumentException("message");
        if (dataOutputStream == null)
            throw new IllegalArgumentException("dataOutputStream");

        final StreamMessageWriter writer = WRITERS.get(message.type());
        if (writer == null)
            throw new RuntimeException(String.format("Internal error: No writer for Message Type '%s'", message.type()));

        writer.write(message, dataOutputStream);
    }

    private void configureWriters() {
        WRITERS.put(MessageType.SIGNALS, new SignalsStreamMessageWriter());
        WRITERS.put(MessageType.BYE, new PlainStreamMessageWriter());
    }
}
