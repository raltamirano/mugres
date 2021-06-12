package mugres.ipc.stream.writers;

import mugres.ipc.protocol.Message;

import java.io.DataOutputStream;
import java.io.IOException;

public class PlainStreamMessageWriter implements StreamMessageWriter {
    @Override
    public void write(final Message message, final DataOutputStream dataOutputStream) throws IOException {
        writeMessageType(message.type(), dataOutputStream);
    }
}
