package mugres.ipc.stream.writers;

import mugres.core.common.Signal;
import mugres.ipc.protocol.messages.SignalsMessage;

import java.io.DataOutputStream;
import java.io.IOException;

public class SignalsStreamMessageWriter implements StreamMessageWriter<SignalsMessage> {
    @Override
    public void write(final SignalsMessage message, final DataOutputStream dataOutputStream) throws IOException {
        writeMessageType(message.type(), dataOutputStream);
        dataOutputStream.writeInt(message.signals().size());
        for(final Signal s : message.signals().signals())
            dataOutputStream.writeInt(s.pack());
    }
}
