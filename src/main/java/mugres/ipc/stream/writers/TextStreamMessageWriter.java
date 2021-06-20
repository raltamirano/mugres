package mugres.ipc.stream.writers;

import mugres.ipc.protocol.messages.TextMessage;

import java.io.DataOutputStream;
import java.io.IOException;

public class TextStreamMessageWriter implements StreamMessageWriter<TextMessage> {
    @Override
    public void write(final TextMessage message, final DataOutputStream dataOutputStream) throws IOException {
        writeMessageType(message.type(), dataOutputStream);
        dataOutputStream.writeUTF(message.text());
    }
}
