package mugres.ipc.stream.writers;

import mugres.ipc.protocol.messages.SetPartyMessage;

import java.io.DataOutputStream;
import java.io.IOException;

public class SetPartyStreamMessageWriter implements StreamMessageWriter<SetPartyMessage> {
    @Override
    public void write(final SetPartyMessage message, final DataOutputStream dataOutputStream) throws IOException {
        writeMessageType(message.type(), dataOutputStream);
        dataOutputStream.writeInt(message.instrument().getId());
    }
}
