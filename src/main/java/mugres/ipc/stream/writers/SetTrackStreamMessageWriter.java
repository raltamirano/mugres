package mugres.ipc.stream.writers;

import mugres.ipc.protocol.messages.SetTrackMessage;

import java.io.DataOutputStream;
import java.io.IOException;

public class SetTrackStreamMessageWriter implements StreamMessageWriter<SetTrackMessage> {
    @Override
    public void write(final SetTrackMessage message, final DataOutputStream dataOutputStream) throws IOException {
        writeMessageType(message.type(), dataOutputStream);
        dataOutputStream.writeInt(message.instrument().id());
    }
}
